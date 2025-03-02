import 'mdb-react-ui-kit/dist/css/mdb.min.css';
import logo from './logo.svg';
import Login from './components/Login';
import Register from "./components/Register";
import Feed from './components/Feed';
import Posts from './components/Posts';
import Profile from './components/Profile';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import './App.css';
import NewPost from './components/NewPost';
import Navbar from './components/Navbar';

function App() {
  return (
    (
      <Router>
        <Navbar />
        <main className="main-content">
          <Routes>
              <Route path="/register" element={<Register />} />
              <Route path="/login" element={<Login />} />
              <Route path="/feed" element={<Feed />} />
              <Route path="/posts" element={<Posts />} />
              <Route path="/new-post" element={<NewPost />} />
              <Route path="/profile/:username" element={<Profile />} />
          </Routes>
          </main>
   </Router>
  ));
}

export default App;
