// A '.tsx' file enables JSX support in the TypeScript compiler, 
// for more information see the following page on the TypeScript wiki:
// https://github.com/Microsoft/TypeScript/wiki/JSX

import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { Route } from 'react-router-dom';
import { UserProfile } from '../messaging/MessageTypes';
import * as Messenger from '../messaging/Messenger';
let MessengerAPI = Messenger.Messenger;

interface LoginUserState {
    username: string;
    password: string;

    isLogging: LoginState;
}

enum LoginState {
    Input = 1,
    Logging,
    BadPwd
}

export class Login extends React.Component<RouteComponentProps<{}>, LoginUserState> {
    constructor() {
        super();
        this.state = { username: "", password: "", isLogging: LoginState.Input };
    }

    public render() {
        return <div>
            <h1>Login user</h1>
            {(this.state.isLogging == LoginState.BadPwd) &&
                <div>Unable to login, please check your user/password</div>
            }
            <form className="container">
                <div className="form-group">
                    <label htmlFor="username">User name</label>
                    <input type="text" className="form-control" value={this.state.username} pattern="([A-z0-9A-z\s]){4,}" id="username" aria-describedby="emailusername" required placeholder="Enter username" onChange={(event) => this.handleUserInput(event)} />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <input type="password" className="form-control" id="password" aria-describedby="pwdhelp" placeholder="Password" pattern=".{6,}" required value={this.state.password} onChange={(event) => this.handleUserInput(event)} />
                    <small id="pwdhelp" className="form-text text-muted">At least 6 characters</small>
                </div>
                <button type="button" className="btn btn-primary" onClick={() => { this.doLogin() }} disabled={this.state.isLogging == LoginState.Logging}>Login</button>
            </form>
            </div>;
    }

    handleUserInput(event: React.FormEvent<HTMLInputElement>) {
        const target = event.currentTarget;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.id;
        this.setState({
            [name]: value
        } as any);
    }

    doLogin() {
        console.log("Login user", this.state);

        this.setState({ isLogging: LoginState.Input });

        var username = this.state.username;
        var password = this.state.password;

        MessengerAPI.login(username, password)
            .then(data => {
                console.log("Login done created");

                this.props.history.push('/contacts')

                //Route(
                // redirect to chat ! 
            })
            .catch(e => {
                console.error("unable to login profile: ", e);
                this.setState({ isLogging: LoginState.BadPwd });
            });;
    }
}
