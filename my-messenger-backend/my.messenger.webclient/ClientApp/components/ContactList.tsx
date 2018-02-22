
import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { Link, NavLink } from 'react-router-dom';
import { Conversation } from './Conversation';
import * as Messenger from '../messaging/Messenger';
import { UserProfile } from '../messaging/MessageTypes';
let MessengerAPI = Messenger.Messenger;

interface IContactListProps {
    handleStartConversation(event: UserProfile): void;
}

export class ContactList extends React.Component<IContactListProps, {}> {
    constructor() {
        super();
    }

    public render() {
        return <div className="container-fluid"> 
            <div className="row">
                <div className=".col-12 .col-sm-12">
                    <h1>Contacts</h1>
                    <p>Select an existig contact or add a new one, by <Link to={'/searchuser'}>searching</Link></p>
                </div>
            </div>
            <div className="row">
                <div className="col-xs-8 col-sm-8 col-md-8 col-lg-8">
                    <b>UserName</b>
                </div>
                <div className="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                    <b>Message</b>
                </div>
            </div>
            {Object.keys(MessengerAPI.Contacts).map(id =>
                <div className="row" key={id}>
                    <div className="col-xs-8 col-sm-8 col-md-8 col-lg-8">
                        {MessengerAPI.Contacts[id].username}
                    </div>
                    <div className="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                        <button className="btn btn-primary" onClick={() => { this.props.handleStartConversation(MessengerAPI.Contacts[id]) }}>Message</button>
                    </div>
                </div>
            )}
        </div>;
    }
}
