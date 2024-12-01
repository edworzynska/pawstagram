import React, { useState, useEffect } from 'react';
import axios from "../axios"; 
import apiClient from '../ApiClient';

function Posts() {
    const [posts, setPosts] = useState([]);
    const [error, setError] = useState(null);
    
    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const response = await apiClient.get("/posts");
                setPosts(response.data);
                console.log(response);
        
            } catch (err) {
            
                if (err.response) {
                    setError(err.response.data || "An error occurred.");
                } else {
                    setError("Network error. Please try again.");
                }
            }
        };

        fetchPosts(); 
    }, []); 

    return (
        <div>
            {error && <p>{error}</p>} 
            <h2>Feed</h2>
            <pre>
                {posts ? JSON.stringify(posts, null, 2) : "Loading..."}
            </pre>
        </div>
    );
}

export default Posts;
