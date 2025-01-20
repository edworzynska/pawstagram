# Pawstagram API

__Pawstagram__ is a social media platform inspired by Instagram and designed for sharing posts about pets. This __RESTful API__ supports user management, post creation, comments, and follows, with integrated __security__ and __transaction__ tracking features. The desktop application is being developed using __React__ and it's in progress.

## Tech Stack

- **Java**: Primary programming language.
- **Spring Boot**: Framework for building the RESTful services.
- **PostgreSQL**: Primary database.
- **H2 Database**: Used for testing purposes.
- **AWS S3**: For storing profile pictures and posts.
- **React**: Frontend (desktop app, in progress).

  
## API Functionality   

### User Management
- **User Registration**: Create a new account with email, username, and password validation.
- **Login**: Secure login functionality for users.
- **Profile Picture Upload**: Upload and store profile pictures in AWS S3.
- **Bio Management**: Add or update a user bio.
- **View User Info**: Retrieve user details, including profile pictures.   

  
### Post Management
- **Create Post**: Upload a post (pictures) with an optional description.
- **View Post**: Get post.
- **Delete Post**: Remove a post by the authorised user.
- **User Posts**: View all posts by a specific user.
- **Feed**: View posts from followed users.
- **All Posts**: Paginated retrieval of all posts.
  
### Comment Management
- **Add Comment**: Add a comment to a post.
- **View Comments**: Get all comments on a post.

### Follow Management
- **Follow/Unfollow**: Follow or unfollow other users.
- **View Followers/Following**: View a list of followers or following users.
  
### Security Features

- **Authentication**: User authentication is managed securely.
- **Authorisation**: Actions are restricted to authenticated users.
  
## Key Learnings

This project gave me a valuable experience in critical areas:
- **AWS S3 Integration**: A major learning was integrating AWS S3 for managing media uploads. This involved configuring buckets, setting up appropriate permissions, and handling file uploads securely from the backend.
- **Spring Boot and React**L The project deepened my understanding of building robust RESTful APIs using Spring Boot and connecting them with a React frontend.
- **Agile Development**: The project was planned and executed using agile principles, which involved breaking down the development process into smaller, manageable sprints. This approach facilitated iterative improvement, ensuring a more adaptable and resilient development process.


## Installation and Setup

### Prerequisites
- Java JDK 21
- Gradle
- PostgreSQL (for production)
- AWS account (for S3 storage) - environmental variables need to be defined: access key and secret key

### Steps
#### Running the Backend:

- Clone the Repository:   
<code>git clone https://github.com/edworzynska/pawstagram.git</code>   
- Create PostgreSQL Database:   
  Set up a new PostgreSQL database for the application.    
- Configure AWS S3:    
Ensure your AWS S3 credentials and bucket configuration are set up correctly. You can define your AWS credentials as environmental variables (ACCESS_KEY and SECRET_KEY). Refer to this [tutorial](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-envvars.html)   on setting environment variables for guidance:   
    
- Build and Run the Application: Navigate to the project directory and use Gradle to build and run the app:   
<code>./gradlew build</code> to build the app and run the tests      
<code>./gradlew bootrun </code>   
The application will start on the default port: http://localhost:8080.   

#### Running the Frontend:

- Run the React App: While the frontend is incomplete, you can start the development server for preview purposes:   
<code>npm install</code>   
<code>npm start</code>   
- The app will open in your default browser.

### Future Work

- Completing the React frontend.
- Enhancing UI/UX for a better user experience.
- Creating an iOS application.
- Adding more features like notifications and direct messaging.
