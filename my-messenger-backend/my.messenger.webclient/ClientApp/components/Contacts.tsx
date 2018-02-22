

// A '.tsx' file enables JSX support in the TypeScript compiler, 
// for more information see the following page on the TypeScript wiki:
// https://github.com/Microsoft/TypeScript/wiki/JSX

import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { Link, NavLink } from 'react-router-dom';
import { UserProfile } from '../messaging/MessageTypes';
import { Conversation } from './Conversation';
import { ContactList } from './ContactList';
import * as Messenger from '../messaging/Messenger';
let MessengerAPI = Messenger.Messenger;

interface ContactsState {
    selectedUserProfile?: UserProfile;
}

export class Contacts extends React.Component<RouteComponentProps<{}>, ContactsState> {
    constructor() {
        super();
        this.state = { selectedUserProfile: undefined };
    }

    public render() {

        var conversation;

        if (this.state.selectedUserProfile != null) {
            conversation = <Conversation userProfile={this.state.selectedUserProfile} />;
        } else {
            conversation = <text>Select a contact</text>;
        }

        return <div className="container-fluid">
            <div className="row">
                <div className="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                    <ContactList handleStartConversation={up => this.startConversation(up)} />
                </div>
                <div className="col-xs-8 col-sm-8 col-md-8 col-lg-8">
                    {conversation}
                </div>
            </div>
        </div>;
    }
    
    startConversation(up: UserProfile) {
        this.setState({ selectedUserProfile: up });
    }
    
}
