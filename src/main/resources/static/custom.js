// blog.js
function loadBlogContent(postId) {
    console.log(`Fetching blog post ${postId}`)
    window.open(`blog/post.html?post_id=${postId}`, '_blank');
}

document.addEventListener('DOMContentLoaded', () => {
    // 使用事件委托绑定事件到父元素,可以将监听绑定到未来创建的元素上
    document.getElementById('blog-post-list-zgz').addEventListener('click', function (event) {
        // 确定点击的是类名为 blog-post-link 的元素
        if (event.target && event.target.matches('.blog-post-link')) {
            event.preventDefault(); // 阻止链接的默认行为
            const postId = event.target.getAttribute('data-post-id');
            loadBlogContent(postId);
        }
    });
});



async function loadBlogDiv() {
    try {
        // 等待 fetch 请求完成
        const response = await fetch('/api/blogs/blog_general');

        // 检查 response 是否成功
        if (!response.ok) {
            throw new Error('Network response was not ok ' + response.statusText);
        }

        const data = await response.json();
        // 使用获取到的数据
        data.forEach(post => {
            const blogPostHTML = createBlogPostHTML(post);
            document.getElementById('blog-post-list-zgz').innerHTML += blogPostHTML;
        });
    } catch (error) {
        console.error('There has been a problem with your fetch operation:', error);
    }
}


// Function to create HTML block for each post
function createBlogPostHTML(post) {
    // Extract the name and head for each post
    const title = post.name.replace('.md', ''); // Removing the .md extension
    // 去除UUID
    var title_string = title.replace(/^[0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12}-/g, '');
    var content = post.head.replace('›', ''); // 去除›
    //使用正则表达式去除前面的多个#
    content = content.replace(/^(#)+/, '');
    // content = content.replace('/^#+/', '');



    // Create the HTML block, using template literals
    return `
    <div class="u-blog-post u-container-style u-repeater-item">
      <div class="u-container-layout u-similar-container u-valign-top u-container-layout-1">
        <!--blog_post_header-->
        <h2 class="u-blog-control u-text u-text-1">
         ${title_string}
        </h2><!--/blog_post_header-->
        
          <img alt="" class="u-blog-control u-expanded-width u-image u-image-default u-image-1"
               src="images/8ad73f3c.jpeg"><!--/blog_post_image-->
      
        <div class="u-blog-control u-post-content u-text u-text-2 fr-view">${content}
        </div><!--/blog_post_content--><!--blog_post_metadata-->
        <div class="u-blog-control u-metadata u-metadata-1"><!--blog_post_metadata_date-->
          <span class="u-meta-date u-meta-icon">Nov 21, 2023</span><!--/blog_post_metadata_date-->
        </div><!--/blog_post_metadata--><!--blog_post_readmore-->
        <a href="#" data-post-id="${title}"
           class="blog-post-link u-active-none u-blog-control u-border-2 u-border-no-left u-border-no-right u-border-no-top u-border-palette-1-base u-btn u-btn-rectangle u-button-style u-hover-none u-none u-btn-1">Read
          More</a>
      </div>
    </div>`;
}

window.onload = function() {
    loadBlogDiv();

};