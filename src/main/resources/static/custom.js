// blog.js
function loadBlogContent(postId) {
    console.log(`Fetching blog post ${postId}`)
    window.open(`blog/post.html?post_id=${postId}`, '_blank');
}

// 绑定点击事件到所有具有'class="blog-post-link"'的链接
document.addEventListener('DOMContentLoaded', () => {
    const links = document.querySelectorAll('.blog-post-link');
    links.forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault(); // 阻止链接的默认行为
            const postId = this.getAttribute('data-post-id');
            loadBlogContent(postId);
        });
    });
});