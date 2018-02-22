import * as React from 'react';
import { Link, NavLink } from 'react-router-dom';
import { MessengerState } from '../messaging/MessageTypes';
import * as Messenger from '../messaging/Messenger';
let MessengerAPI = Messenger.Messenger;

interface NavMenuState {
    isLogged: boolean;
}

export class NavMenu extends React.Component<{}, NavMenuState> {
    idOnMessengerStateChange: number;
    constructor() {
        super();
        this.state = { isLogged: false };
    }

    public componentDidMount() {
        console.log("menu subscribing to OnMessengerStateChange");
        this.idOnMessengerStateChange = MessengerAPI.OnMessengerStateChange.on((s) => this.onMessengerStateChanges(s));
    }

    public componentWillUnmount() {
        MessengerAPI.OnMessengerStateChange.off(this.idOnMessengerStateChange);
    }

    private onMessengerStateChanges(value?: MessengerState) {
        var r = (value != MessengerState.Offline);
        this.setState({ isLogged: r });
    }

    public render() {
        return <div className='main-nav'>
                <div className='navbar navbar-inverse'>
                <div className='navbar-header'>
                    <button type='button' className='navbar-toggle' data-toggle='collapse' data-target='.navbar-collapse'>
                        <span className='sr-only'>Toggle navigation</span>
                        <span className='icon-bar'></span>
                        <span className='icon-bar'></span>
                        <span className='icon-bar'></span>
                    </button>
                    <Link className='navbar-brand' to={ '/' }>my.messenger.webclient!!</Link>
                </div>
                <div className='clearfix'></div>
                <div className='navbar-collapse collapse'>
                    {this.state.isLogged &&
                        <ul className='nav navbar-nav'>
                            <li>
                                <NavLink to={'/contacts'} activeClassName='active'>
                                    <span className='glyphicon glyphicon-th-list'></span> Contacts
                                    </NavLink>
                            </li>
                            <li>
                                <NavLink to={'/searchuser'} activeClassName='active'>
                                    <span className='glyphicon glyphicon-th-list'></span> Search User
                            </NavLink>
                            </li>
                            <li>
                                <NavLink to={'/dev'} activeClassName='active'>
                                    <span className='glyphicon glyphicon-th-list'></span> Dev
                            </NavLink>
                            </li>
                        </ul>
                    }
                    {!this.state.isLogged &&
                        <ul className='nav navbar-nav'>
                            <li>
                                <NavLink to={'/'} exact activeClassName='active'>
                                    <span className='glyphicon glyphicon-home'></span> Home
                                </NavLink>
                            </li>
                            <li>
                                <NavLink to={'/login'} activeClassName='active'>
                                    <span className='glyphicon glyphicon-th-list'></span> Login
                                </NavLink>
                            </li>
                            <li>
                                <NavLink to={'/registeruser'} activeClassName='active'>
                                    <span className='glyphicon glyphicon-th-list'></span> Create Account
                                </NavLink>
                            </li>
                        </ul>
                    }
                </div>
            </div>
        </div>;
    }
}
