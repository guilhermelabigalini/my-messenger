import * as React from 'react';
import { Link, NavLink } from 'react-router-dom';
import { RouteComponentProps } from 'react-router';

export class Home extends React.Component<RouteComponentProps<{}>, {}> {
    public render() {
        return <div>
            <h1>Welcome to mymessenger</h1>
            <p>Welcome to your new single-page application, built with:</p>
            <p>To help you get started, select the links below:</p>
            <ul>
                <li><strong><Link to={'/login'}>Login</Link></strong> with your existing account.</li>
                <li><strong><Link to={'/registeruser'}>Create Account</Link></strong> to setup your profile</li>
            </ul>
        </div>;
    }
}
