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

function fetchAndDisplayPostContent(postId) {
    fetch(`/api/blogs/${postId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(html => {
            const postContentDiv = document.getElementById('post-content');
            if (postContentDiv) {
                postContentDiv.innerHTML = html;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            const postContentDiv = document.getElementById('post-content');
            if (postContentDiv) {
                postContentDiv.innerHTML = `<p>Error loading blog post: ${error.message}</p>`;
            }
        });
}

// 从URL获取postId
const postId = getQueryVariable('post_id');

if (postId) {
    fetchAndDisplayPostContent(postId);
} else {
    console.error('Post ID not found in URL');
}
