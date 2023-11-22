›# TODO:每一帧有多个图元的绘制
*Considering:*  
- [x] 绘制步骤，详细给出。 🛫 2023-10-18 ⏳ 2023-10-19 ✅ 2023-10-25
# 分辨率相关的操作
1. 直线抗锯齿的距离计算。
2. 内部三角形的纹理坐标计算。

# 直线抗锯齿的距离计算
$u=U(x,y)v = V(x,y)$

对于xy空间中任意一点$x_0,y_0$
$$
U(x_0, y_0) = u_0
$$

$$
V(x_0, y_0) = v_0
$$



给出距离公式。

$$
d = \frac{|U(x_0, y_0)|}{\sqrt{\left(\frac{\partial U}{\partial x}\right)^2 + \left(\frac{\partial U}{\partial y}\right)^2}}
$$


```glsl
else if (flag == 0) {  
    vec2 uv = texCoord.xy;  
    vec2 px = dFdx(uv);  
    vec2 py = dFdy(uv);  
    float minComponent = uv.x/sqrt(px.x* px.x + py.x*py.x);  
    if (minComponent > 1e-2 && minComponent < 1 + 1e-2) {//设置下界是因为曲线直边dis=0  
                                                         //抗锯齿区域  
                                                         float alpha = minComponent;  
                                                         alpha = 1 - alpha;  
                                                         alpha = alpha * 0.8;  
                                                         outColor = vec4(1, 1, 1, alpha);  
    }  
    else {  
        outColor = vec4(color, 1);  
    }
```

# 移动端能耗
Ali, F., Simoens, P., Verbelen, T., Demeester, P. & Dhoedt, B. Mobile Device Power Models for Energy Efficient Dynamic Offloading at Runtime. _Journal of Systems and Software_ **113**, (2015).
指出内存读写对整体耗能会产生最多20%的影响

# skia PR选择
## 逻辑
作为圆角矩形
	绘制圆角矩形
	绘制椭圆
>[!SkRRect]
>描述了不同种类的`SkRRect`形状。

1. **kEmpty_Type**:
   - 这代表一个空的`SkRRect`。它的宽度和高度都是零。

2. **kRect_Type**:
   - 这代表一个标准的矩形，它有非零的宽度和高度，但所有的角都是尖锐的，也就是说圆角半径为零。

3. **kOval_Type**:
   - 这代表一个椭圆形。它的宽度和高度都是非零的，并且它的每个角都被填充为圆角。如果宽度和高度相等，这就是一个圆。

4. **kSimple_Type**:
   - 这代表一个简单的圆角矩形，它有非零的宽度和高度，并且所有的角都有相同的半径。

5. **kNinePatch_Type**:
   - 这代表一个九片矩形（Nine-Patch）。它的宽度和高度都是非零的，且圆角半径是轴对齐的。九片矩形是用于可拉伸图像的技术，但在这里它指的是具有各种圆角的矩形。

6. **kComplex_Type**:
   - 这代表一个复杂的圆角矩形，它有非零的宽度和高度，且每个角都可以有任意的半径，这意味着每个角的圆角可以是不同的。

7. **kLastType**:
   - 这是一个辅助的枚举值，表示所有类型中的最大值。在某些编程场景中，这可以用于范围检查或迭代所有类型。

## PathRender
1. GrSoftwarePathRenderer
2. GrAAConvexPathRenderer
3. GrAAHairLinePathRenderer
4. GrAALinearizingConvexPathRenderer
5. GrDashLinePathRenderer
6. GrDefaultPathRenderer
7. GrSmallPathRenderer
8. GrTriangulatingPathRenderer
9. GrTessellationPathRenderer

===

1. ***GrAAConvexPathRenderer***: 用于渲染抗锯齿的凸路径。由于凸路径的简单性，这个渲染器可以高效地渲染这些路径。
    
2. ***GrAALinearizingConvexPathRenderer***: 用于渲染需要线性化的抗锯齿的凸路径，例如包含贝塞尔曲线的路径。
    
3. **GrAAHairLinePathRenderer**: 专门用于渲染细线（例如线宽为1像素的线）。
    
4. **GrDashLinePathRenderer**: 用于渲染虚线路径。
    
5. **GrAAQuadPerEdgePathRenderer**: 为每条边使用一个四边形来渲染路径。这种方法在某些GPU上可能比其他方法更高效。
    
6. **GrAATrianglePathRenderer**: 使用三角形填充技术来渲染路径。

8. **SoftwarePathRenderer**
```
GrPathRenderer* GrDrawingManager::getSoftwarePathRenderer() {

if (!fSoftwarePathRenderer) {

fSoftwarePathRenderer.reset(

new GrSoftwarePathRenderer(fContext->priv().proxyProvider(),

fOptionsForPathRendererChain.fAllowPathMaskCaching));

}

return fSoftwarePathRenderer.get();

}

  

GrCoverageCountingPathRenderer* GrDrawingManager::getCoverageCountingPathRenderer() {

if (!fPathRendererChain) {

fPathRendererChain = std::make_unique<GrPathRendererChain>(fContext,

fOptionsForPathRendererChain);

}

return fPathRendererChain->getCoverageCountingPathRenderer();

}

  

GrPathRenderer* GrDrawingManager::getTessellationPathRenderer() {

if (!fPathRendererChain) {

fPathRendererChain = std::make_unique<GrPathRendererChain>(fContext,

fOptionsForPathRendererChain);

}

return fPathRendererChain->getTessellationPathRenderer();

}
```
## drawStrokedLine


# 绘制时间占比分析
subpass0即使只绘制三角形，也存在较大的时间消耗
只保留subpass0，对时间优化无影响

确定主要绘制时间来自于subpass0，猜想是否因为想image中绘制需要较大的时间消耗
确定image的内存是device local的， 猜测绘制时间消耗来自于绘制流程 
text100 30ms,空绘制10ms
定位：由于我们的三角形很大，因此执行的fs很多，从而引起时间上的较大消耗（即使是空绘制）。

**启发：能否避免在内部三角形执行复杂逻辑**

# 细分与分辨率
6条二次贝塞尔曲线是tess的极限

![image.png](https://obsidian-picture-1313051055.cos.ap-nanjing.myqcloud.com/Obsidian/20231024204253.png)
![image.png](https://obsidian-picture-1313051055.cos.ap-nanjing.myqcloud.com/Obsidian/20231024204559.png)
# Deep in skia PR
## 一段贝塞尔曲线
#blinnInSkia 
![image.png](https://obsidian-picture-1313051055.cos.ap-nanjing.myqcloud.com/Obsidian/20231025125858.png)
一段曲线对应三个三角形，矩形区域的另外两个三角形用于直线边处理。
曲线边界对应的小矩形由原始边平移1pixel得到，同样的，控制点也要移动1pixel，然后计算新的纹理坐标。

直线和曲线抗锯齿使用统一的公式，只需要将直线纹理坐标设置为（0，-1）（0，-1）（0，0），这样的话u=0，f=u^2-v的抗锯齿公式从而适用。

## 多段
![image.png](https://obsidian-picture-1313051055.cos.ap-nanjing.myqcloud.com/Obsidian/20231025130311.png)
内部三角形设置inQuadEdge.wz实现抗锯齿，但不清楚具体作用，因为内部边不需要抗锯齿，直线边不会设置inQuadEdge.wz。