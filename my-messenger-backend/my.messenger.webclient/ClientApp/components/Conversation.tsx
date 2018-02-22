
import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import * as Messenger from '../messaging/Messenger';
import { UserProfile, Destination, Message, ContactInfo, DestinationType } from '../messaging/MessageTypes';
let MessengerAPI = Messenger.Messenger;

interface ConversationState {
    textToSend: string;
    maxMessages: number;
}

interface IConversationProps {
    userProfile: UserProfile;
}

export class Conversation extends React.Component<IConversationProps, ConversationState> {

    constructor() {
        super();
        this.state = { textToSend: "", maxMessages: 10 };
    }

    private getScreenName(id: string): string {
        var c = MessengerAPI.Contacts[id];
        if (c != null) {
            return c.username;
        }
        return id;
    }

    public render() {

        var style: React.CSSProperties = {
            overflowY: "scroll",
            backgroundColor: "#ffaa00",
            height: "450px"
        };

        var fromMe: React.CSSProperties = {
            backgroundColor: "#52a6ee",
        };

        var toMe: React.CSSProperties = {
            backgroundColor: "#c5e0bd",
        };

        var contactInfo: ContactInfo = MessengerAPI.MessageLog[this.props.userProfile.id];
        var log: Message[] = [];
        var myId = MessengerAPI.LoggedUserId;

        if (contactInfo != null) {
            log = contactInfo.messages;
        };

        // log = log.slice(Math.max(log.length - this.state.maxMessages, 1))

        // return 
        return <div className="container-fluid"> 
            <div className="row">
                <div className="col">
                    <b>Conversation with {this.props.userProfile.username}</b>
                </div>
            </div>           
            <div className="row">
                <div className="col">
                    <div style={style}>
                        {log.map((msg, index) =>
                            <div key={index} style={myId == msg.fromUserId ? fromMe : toMe}>{msg.sentAt} - {msg.body}</div>
                        )}
                    </div>
                </div>
            </div>
            <div className="row">
                <div className="col">
                    <div className="form-group">
                        <label htmlFor="searchBox">Name:</label>
                        <input id="searchBox" className="form-control" type="text" name="name" value={this.state.textToSend} onChange={(e) => { this.handleChangeText(e) }} />
                        <button className="btn btn-primary" onClick={() => { this.sendMessage() }}>Send</button>
                    </div>
                </div>
            </div>
        </div>;
    }

    handleChangeText(event: React.FormEvent<HTMLInputElement>) {
        this.setState({ textToSend: event.currentTarget.value });
    }

    sendMessage() {

        var to: Destination = {
            id: this.props.userProfile.id,
            type: DestinationType.User
        };

        MessengerAPI.sendMessage(to.id, to.type, this.state.textToSend);

        this.setState({ textToSend: "" });
    }
    
}
