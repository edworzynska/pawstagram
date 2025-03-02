import React, { useState } from "react";
import axios from "axios";
import apiClient from "../ApiClient";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault(); 
    try {
     
      const formData = new URLSearchParams();
      formData.append("email", email);
      formData.append("password", password);

      await axios.post(
        "http://localhost:8080/login",
        formData,
        {
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          withCredentials: true, 
        });

      const response = await apiClient.get("/api/logged-user");
      localStorage.setItem("username", response.data.username);
      localStorage.setItem("user", JSON.stringify(response.data));

      window.location.href = "/feed";
      console.log("Login successful:", response.data);

    } catch (err) {
      console.error("Login failed:", err.response?.data || err.message);
      setError("Invalid email or password. Please try again.");
    }
  };

  return (
    <div>
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Email:</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Password:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        {error && <p style={{ color: "red" }}>{error}</p>}
        <button type="submit">Login</button>
      </form>
    </div>
  );
};

export default Login;
