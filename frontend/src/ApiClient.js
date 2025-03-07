import axios from "axios";

const apiClient = axios.create({
  baseURL: "http://localhost:8080", 
  withCredentials: true, 
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default apiClient;