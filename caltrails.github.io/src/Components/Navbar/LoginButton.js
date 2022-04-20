import React from "react";
import { useAuth0 } from "@auth0/auth0-react";
import {AwesomeButton} from 'react-awesome-button';
import 'react-awesome-button/dist/themes/theme-blue.css';
import "./button.css";

const LoginButton = () => {
  const { loginWithRedirect } = useAuth0();

  return <AwesomeButton type="primary" size="medium" onClick={() => loginWithRedirect()}>Log In</AwesomeButton>;
};

export default LoginButton;