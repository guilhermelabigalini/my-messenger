// A '.tsx' file enables JSX support in the TypeScript compiler, 
// for more information see the following page on the TypeScript wiki:
// https://github.com/Microsoft/TypeScript/wiki/JSX

import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import * as Messenger from '../messaging/Messenger';
import { StreamMessage, StreamMessageType, UserProfile } from "../messaging/MessageTypes";
let MessengerAPI = Messenger.Messenger;

export class DevPanel extends React.Component<RouteComponentProps<{}>, {}> {
    constructor() {
        super();
    }

    public render() {
        return <div>
            <h1>Dev Panel</h1>
            <div id="log">
                <button type="button" className="btn btn-primary" onClick={() => { this.printInfo() }}>PrintInfo</button>

                <button type="button" className="btn btn-primary" onClick={() => { this.connect() }}>Connect</button>

                <button type="button" className="btn btn-primary" onClick={() => { this.login("user1") }}>Login with user1</button>

                <button type="button" className="btn btn-primary" onClick={() => { this.login("user2") }}>Login with user2</button>
            </div>
        </div>;
    }

    printInfo() {
        MessengerAPI.debug();
    }

    connect() {
        MessengerAPI.connect();
    }

    login(u: string) {
        MessengerAPI.login(u, "123456");
    }
}
