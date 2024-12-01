import React, { useState } from "react";
import apiClient from "../ApiClient"; 

const NewPost = () => {
  const [description, setDescription] = useState(""); 
  const [photo, setPhoto] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!photo) {
      alert("Please add a photo.");
      return;
    }

    const formData = new FormData();
    formData.append("file", photo); 
    if (description.trim()) {
      formData.append("description", description); 
    }

    try {
    
      const response = await apiClient.post("/api/add-post", formData, {
        headers: {
          "Content-Type": "multipart/form-data", 
        },
      });
      console.log("Post created successfully:", response.data);
      window.location.href = `/feed`;
    } catch (error) {
      console.error("Error posting new post:", error);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Write a description..."
        />
      </div>
      <div>
        <input
          type="file"
          accept="image/*"
          onChange={(e) => setPhoto(e.target.files[0])}
        />
      </div>
      <button type="submit">Post</button>
    </form>
  );
};

export default NewPost;
