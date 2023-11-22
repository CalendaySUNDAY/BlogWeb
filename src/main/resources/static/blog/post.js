// post.js - 这个脚本应该被包含在post.html中

function getQueryVariable(variable) {
    const query = window.location.search.substring(1);
    const vars = query.split('&');
    for (let i = 0; i < vars.length; i++) {
        const pair = vars[i].split('=');
        if (decodeURIComponent(pair[0]) === variable) {
            return decodeURIComponent(pair[1]);
        }
    }
    return null;
}

function loadBlogContent(postId) {
    fetch(`/api/blogs/${postId}`)
        .then(response => response.text())
        .then(markdown => {
            // 使用marked.js将Markdown转换为HTML
            const html = marked.parse(markdown);
            console.log(html)
            // 将HTML内容放到页面的某个元素中
            document.getElementById('post-content').innerHTML = html;
            // 如果您使用KaTeX，您可以在此处理数学公式
            renderMathInElement(document.getElementById('post-content'), {
                delimiters: [
                    { left: "$$", right: "$$", display: true }, // 多行公式
                    { left: "$", right: "$", display: false }, // 单行公式
                ],
            });

        })
        .catch(error => {
            console.error('Error loading blog content:', error);
        });
}


// 从URL获取postId
const postId = getQueryVariable('post_id');

if (postId) {
    loadBlogContent(postId);
} else {
    console.error('Post ID not found in URL');
}
