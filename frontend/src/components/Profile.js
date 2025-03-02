import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import apiClient from "../ApiClient";
import "./Profile.css";

const Profile = () => {
    // Get the username from the URL
    const { username } = useParams();
    const navigate = useNavigate();

    // For demonstration, we assume the active user's username is stored in localStorage.
    // In a real app, consider using an auth context.
    const activeUsername = localStorage.getItem("username");
    const isOwnProfile = username === activeUsername;

    // State variables
    const [user, setUser] = useState(null);
    const [posts, setPosts] = useState([]);
    const [isEditing, setIsEditing] = useState(false);
    const [newBio, setNewBio] = useState("");
    const [selectedFile, setSelectedFile] = useState(null);
    const [preview, setPreview] = useState("");

    // Fetch user info
    useEffect(() => {
        async function fetchUser() {
            try {
                const response = await apiClient.get(`/api/${username}`);
                setUser(response.data);
                setNewBio(response.data.bio || "");
            } catch (error) {
                console.error("Error fetching user info", error);
            }
        }
        fetchUser();
    }, [username]);

    useEffect(() => {
        async function fetchPosts() {
            try {
                // Call the endpoint for posts by a specific user
                const response = await apiClient.get(`/api/${username}/posts`);
                setPosts(response.data);
            } catch (error) {
                console.error("Error fetching posts", error);
            }
        }
        fetchPosts();
    }, [username]);

    // Handle file input change for updating the profile picture
    const handleFileChange = (e) => {
        const file = e.target.files[0];
        setSelectedFile(file);
        const reader = new FileReader();
        reader.onloadend = () => {
            setPreview(reader.result);
        };
        if (file) {
            reader.readAsDataURL(file);
        }
    };

    // Update the bio using the addBio endpoint
    const handleBioUpdate = async () => {
        try {
            await apiClient.post(`/api/add-bio`, null, { params: { bio: newBio } });
            // Refresh user info after update
            const response = await apiClient.get(`/api/${username}`);
            setUser(response.data);
        } catch (error) {
            console.error("Error updating bio", error);
        }
    };

    // Update the profile picture using the upload-profile-pic endpoint
    const handleProfilePicUpdate = async () => {
        try {
            const formData = new FormData();
            formData.append("file", selectedFile);
            await apiClient.post(`/api/upload-profile-pic`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });
            // Refresh user info after update
            const response = await apiClient.get(`/api/${username}`);
            setUser(response.data);
        } catch (error) {
            console.error("Error updating profile picture", error);
        }
    };

    // Handle saving changes (both bio and profile picture)
    const handleSaveChanges = async (e) => {
        e.preventDefault();
        if (newBio !== user.bio) {
            await handleBioUpdate();
        }
        if (selectedFile) {
            await handleProfilePicUpdate();
        }
        setIsEditing(false);
    };

    // Navigate to detailed view when clicking a post
    const handlePostClick = (postId) => {
        navigate(`/posts/${postId}`);
    };

    return (
        <div className="profile-container">
            {user ? (
                <>
                    <div className="profile-header">
                        <img
                            src={
                                preview ||
                                user.profileImgUrl ||
                                "https://your-default-image-url.com/default-profile.png"
                            }
                            alt={user.username}
                            className="profile-pic"
                        />
                        <div className="profile-info">
                            <h2>{user.username}</h2>
                            {isEditing ? (
                                <textarea
                                    value={newBio}
                                    onChange={(e) => setNewBio(e.target.value)}
                                />
                            ) : (
                                <p>{user.bio}</p>
                            )}
                            {isOwnProfile && (
                                <button onClick={() => setIsEditing(!isEditing)}>
                                    {isEditing ? "Cancel" : "Edit Profile"}
                                </button>
                            )}
                        </div>
                    </div>
                    {isEditing && isOwnProfile && (
                        <form onSubmit={handleSaveChanges} className="edit-form">
                            <div>
                                <label>Update Profile Picture:</label>
                                <input type="file" accept="image/*" onChange={handleFileChange} />
                            </div>
                            <button type="submit">Save Changes</button>
                        </form>
                    )}
                    <div className="profile-posts">
                        {posts && posts.length > 0 ? (
                            <div className="posts-grid">
                                {posts.map((post) => (
                                    <div
                                        key={post.id}
                                        className="post-tile"
                                        onClick={() => handlePostClick(post.id)}
                                    >
                                        <img src={post.imgUrl} alt="Post" />
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <p>No posts yet.</p>
                        )}
                    </div>
                </>
            ) : (
                <div>Loading profile...</div>
            )}
        </div>
    );
};

export default Profile;