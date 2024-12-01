import React, { useEffect, useState } from "react";
import apiClient from "../ApiClient";
import { Modal } from "react-bootstrap";
import { FaHeart, FaComment } from "react-icons/fa";
import "./Feed.css";
import { format } from 'date-fns';

const Feed = () => {
  const [posts, setPosts] = useState([]);
  const [profilePics, setProfilePics] = useState({});
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [showComments, setShowComments] = useState(false);
  const [currentPostId, setCurrentPostId] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [isPostLiked, setIsPostLiked] = useState(false);

  const fetchPosts = async () => {
    setLoading(true);
    try {
      const response = await apiClient.get(`/api/posts`, {
        params: { pageNo: page, pageSize: 10 },
      });
      setPosts((prevPosts) => [...prevPosts, ...response.data.content]);
      setPage((prevPage) => prevPage + 1);
    } catch (error) {
      console.error("Error fetching posts", error);
    }
    setLoading(false);
  };

  const fetchProfilePics = async () => {
    try {
      const usernames = [...new Set(posts.map((post) => post.username))];
      const profilePicMap = {};
      await Promise.all(
        usernames.map(async (username) => {
          if (!profilePics[username]) {
            const response = await apiClient.get(`/api/${username}/profile-pic`);
            profilePicMap[username] = response.data;
          }
        })
      );
      setProfilePics((prevPics) => ({ ...prevPics, ...profilePicMap }));
    } catch (error) {
      console.error("Error fetching profile pictures", error);
    }
  };


  const postComment = async (postId, contents) => {
    try {
      await apiClient.post(`/api/posts/${postId}/add-comment`, contents, {
        headers: {"Content-Type": 'text/plain'},
      });
      
      fetchPosts();
      fetchComments(postId);
    } catch (error) {
      console.error("Error posting comment", error);
    }
  };

  const likePost = async (postId) => {
    try {
      await apiClient.post(`/api/posts/${postId}/like`);
      fetchPosts();
    } catch (error) {
      console.error("Error while posting a like", error);
    }
  };

  const unlikePost = async (postId) => {
    try {
        await apiClient.delete(`/api/posts/${postId}/unlike`);
        fetchPosts();
    } catch (error) {
        console.error("Error while unliking the post", error);
    }
  }

  const fetchComments = async (postId) => {
    try {
      const response = await apiClient.get(`/api/posts/${postId}/comments`);
      if (Array.isArray(response.data)) {
        setComments(response.data);
      } else {
        setComments([]);
      }
      setCurrentPostId(postId);
      setShowComments(true);
    } catch (error) {
      console.error("Error fetching comments", error);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return format(date, 'MMM dd, yyyy HH:mm');  
  };

  useEffect(() => {
    fetchPosts();
  }, []);

  useEffect(() => {
    if (posts.length > 0) {
      fetchProfilePics();
    }
  }, [posts]);

  return (
    <div className="feed">
      {posts.map((post) => (
        <div className="post" key={post.id}>
          <div className="post-header">
            <img
              src={
                profilePics[post.username] ||
                "https://my-instargam-app.s3.eu-north-1.amazonaws.com/profile-pics/pngimg.com+-+paw_PNG27.png"
              }
              alt={post.username}
              className="profile-pic"
            />
            <a href={`/api/${post.username}`}>{post.username}</a>
          </div>
          <img src={post.imgUrl} alt="Post" className="post-image" />
          <div className="post-body">
            <p>{post.description}</p>
            <div>
              <FaHeart onClick={() => likePost(post.id)} />
              <span>{post.likesCount}</span>
              <FaComment onClick={() => fetchComments(post.id)} />
              <span>{post.commentsCount}</span>
            </div>
            
          </div>
        </div>
      ))}
      {loading && <p>Loading...</p>}
      <button onClick={fetchPosts} disabled={loading}>
        Load More
      </button>


      <Modal show={showComments} onHide={() => setShowComments(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Comments</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {comments.map((comment) => (
            <p key={comment.id}>
              <strong>{comment.username}: </strong>
              {comment.contents}
              <br></br>
              <i>{formatDate(comment.date)}</i>
            </p>
        
          ))}
          <textarea
            placeholder="Write a comment..."
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
          />
          <button
            onClick={() => {
              if (newComment.trim()) {
                postComment(currentPostId, newComment);
                setNewComment("");
              }
            }}
          >
            Post Comment
          </button>
        </Modal.Body>
      </Modal>
    </div>
  );
};

export default Feed;
