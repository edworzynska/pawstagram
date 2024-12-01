import React, {useState} from "react";
import axios from "../axios";
import {
    MDBBtn,
    MDBContainer,
    MDBCard,
    MDBCardBody,
    MDBInput,
    MDBCheckbox,
    MDBCardText,
    MDBCardLink
  }
  from 'mdb-react-ui-kit';

const Register = () => {
    const [email, setEmail] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");

    const handleRegister = async (e) => {
        e.preventDefault();
        try{
            const response = await axios.post(`/register`, null, {
                params: {email, username, password},
            });
            setMessage(response.data);
        } catch (err){
            const errorMessage = err.response?.data;
            setMessage(errorMessage);
            console.log(errorMessage);
        }
    };
    return (
       <MDBContainer fluid className='d-flex align-items-center justify-content-center bg-image' style={{backgroundImage: 'url(https://mdbcdn.b-cdn.net/img/Photos/new-templates/search-box/img4.webp)'}}>
      <div className='mask gradient-custom-3'></div>
      <MDBCard className='m-5' style={{maxWidth: '600px'}}>
      
        <MDBCardBody className='px-5'>
        <form onSubmit={handleRegister}>
          <h2 className="text-uppercase text-center mb-5">Create an account</h2>
          <MDBCardText>{message && <p color="red">Message: {message}</p>}</MDBCardText>
          <MDBInput wrapperClass='mb-4' label='Username' size='lg' id='form1' type='text' value={username}  onChange={(e) => setUsername(e.target.value)}/>
          <MDBInput wrapperClass='mb-4' label='Email address' size='lg' id='form2' type='email' value={email}  onChange={(e) => setEmail(e.target.value)}/>
          <MDBInput wrapperClass='mb-4' label='Password' size='lg' id='form3' type='password' value={password}  onChange={(e) => setPassword(e.target.value)}/>
            
          <div className='d-flex flex-row justify-content-center mb-4'>
            <MDBCheckbox name='flexCheck' id='flexCheckDefault' label='I agree all statements in Terms of service' />
          </div>
          <MDBBtn className='mb-4 w-100 gradient-custom-4' size='lg' >Register</MDBBtn>
          </form>
         <MDBCardText>Already have an account? Log in <MDBCardLink
                href="/login"
            >
                here
            </MDBCardLink></MDBCardText>
        </MDBCardBody>
      </MDBCard>
      
    </MDBContainer>
    );
};

export default Register;