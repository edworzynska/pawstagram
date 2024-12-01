import React, { useState } from 'react';
import {
  MDBContainer,
  MDBTabs,
  MDBTabsItem,
  MDBTabsLink,
  MDBTabsContent,
  MDBTabsPane,
  MDBInput,
  MDBBtn
} from 'mdb-react-ui-kit';

function LoginAndRegister() {
  
  const [justifyActive, setJustifyActive] = useState('tab1');

  
  const handleJustifyClick = (value) => {
    if (value === justifyActive) return; 
    setJustifyActive(value); 
  };

  return (
    <MDBContainer className="p-3 my-5 d-flex flex-column w-50">
    
      <MDBTabs pills justify className='mb-3'>
        <MDBTabsItem>
          <MDBTabsLink onClick={() => handleJustifyClick('tab1')} active={justifyActive === 'tab1'}>
            Login
          </MDBTabsLink>
        </MDBTabsItem>
        <MDBTabsItem>
          <MDBTabsLink onClick={() => handleJustifyClick('tab2')} active={justifyActive === 'tab2'}>
            Register
          </MDBTabsLink>
        </MDBTabsItem>
      </MDBTabs>

     
      <MDBTabsContent>
      
        <MDBContainer>
        <MDBTabsPane show={justifyActive === 'tab1'}>
          <h5>Login Form</h5>
          <MDBInput label="Email" type="email" />
          <MDBInput label="Password" type="password" />
          <MDBBtn>Login</MDBBtn>
        </MDBTabsPane>
        </MDBContainer>

    
        <MDBTabsPane show={justifyActive === 'tab2'}>
          <h5>Register Form</h5>
          <MDBInput label="Username" type="text" />
          <MDBInput label="Email" type="email" />
          <MDBInput label="Password" type="password" />
          <MDBBtn>Register</MDBBtn>
        </MDBTabsPane>
      </MDBTabsContent>
    </MDBContainer>
  );
}

export default LoginAndRegister;
