// A '.tsx' file enables JSX support in the TypeScript compiler, 
// for more information see the following page on the TypeScript wiki:
// https://github.com/Microsoft/TypeScript/wiki/JSX

import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { Link, NavLink } from 'react-router-dom';
import { UserProfile } from '../messaging/MessageTypes';
import * as Messenger from '../messaging/Messenger';
let MessengerAPI = Messenger.Messenger;

enum CreatingState {
    Input = 1,
    Creating,
    Created,
    Failed,
}

interface RegisterUserState  {
    //profile: UserProfile;
    username: string;
    birthDate: string;
    password: string;

    creatingState: CreatingState;
}

export class RegisterUser extends React.Component<RouteComponentProps<{}>, RegisterUserState> {
    constructor() {
        super();
        this.state = { username: "", birthDate: "", password: "", creatingState: CreatingState.Input };
    }

    public render() {
        return <div>
            {(this.state.creatingState == CreatingState.Input || this.state.creatingState == CreatingState.Creating) &&
                <div>
                    <h1>Create your account</h1>
                    <form className="container">
                        <div className="form-group">
                            <label htmlFor="username">User name</label>
                            <input type="text" className="form-control" value={this.state.username} pattern="([A-z0-9A-z\s]){4,}" id="username" aria-describedby="emailusername" required placeholder="Enter username" onChange={(event) => this.handleUserInput(event)} />
                            <small id="emailusername" className="form-text text-muted">This will be your public profile id (between 4-30 digits).</small>
                        </div>
                        <div className="form-group">
                            <label htmlFor="password">Password</label>
                            <input type="password" className="form-control" id="password" aria-describedby="pwdhelp" placeholder="Password" pattern=".{6,}" required value={this.state.password} onChange={(event) => this.handleUserInput(event)} />
                            <small id="pwdhelp" className="form-text text-muted">At least 6 characters</small>
                        </div>
                        <div className="form-group">
                            <label className="form-check-label" htmlFor="birthDate"> Birth date</label>
                            <input type="date" id="birthDate" required className="form-control" value={this.state.birthDate} onChange={(event) => this.handleUserInput(event)} />
                        </div>
                        <button type="submit" className="btn btn-primary" onClick={() => { this.doRegister() }} disabled={this.state.creatingState != CreatingState.Input}>Create Account</button>
                    </form>
                </div>
            }
            {this.state.creatingState == CreatingState.Created &&
                <div>
                    <h1>Your account {this.state.username} has been created! <Link to={'/login'}>Login</Link> to start your session</h1>
                </div>
            }
            {this.state.creatingState == CreatingState.Failed &&
                <div>
                <h1>Unable to create your account, click  <Link to={'/registeruser'}>here</Link> to try again </h1>
                </div>
            }
            </div>;
    }
    
    handleUserInput(event: React.FormEvent<HTMLInputElement>) {
        const target = event.currentTarget;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name =  target.id;
        this.setState({
            [name]: value
        } as any);
    }

    // https://learnetto.com/blog/how-to-do-simple-form-validation-in-reactjs
    doRegister() {
        console.log("Creating user", this.state);
        
        var up: UserProfile = new UserProfile();
        up.birthDate = this.state.birthDate;
        up.username = this.state.username;
        up.password = this.state.password;

        this.setState({ creatingState: CreatingState.Creating });

        MessengerAPI.createUser(up)
            .then(data => {
                console.log("profile created");
                this.setState({ creatingState: CreatingState.Created });
            })
            .catch(e => {
                console.error("unable to create profile: ", e);
                this.setState({ creatingState: CreatingState.Failed });
            });;

    }
    
}
