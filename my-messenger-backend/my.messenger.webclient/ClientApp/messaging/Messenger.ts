
import {
    StreamMessage,
    StreamMessageType,
    Message,
    DestinationType,
    Destination,
    HashTable,
    ContactInfo,
    TransmittedMessage,
    MessageType,
    UserSession,
    UserProfile,
    LoginRequest,
    MessengerState
} from "./MessageTypes";
import { LiteEvent, ILiteEvent } from "./LiteEvent";


class MessengerAPI {

    private readonly BaseAPIUrl: string = "https://my-messenger-backend.azurewebsites.net/";
    private readonly BaseWSAPIUrl: string = "wss://my-messenger-backend.azurewebsites.net/";
    private currentSession?: UserSession;
    private currentUsername?: string;
    private ws?: WebSocket;
    private messengerState: MessengerState;
    private readonly onMessengerStateChange = new LiteEvent<MessengerState>();
    private messageLog: HashTable<ContactInfo>;
    private contacts: HashTable<UserProfile>;

    constructor() {
        this.resetState();
    }

    // Properties
    public get OnMessengerStateChange(): ILiteEvent<MessengerState> { return this.onMessengerStateChange.expose(); }
    public get MessageLog(): HashTable<ContactInfo> { return this.messageLog; }
    public get Contacts(): HashTable<UserProfile> { return this.contacts; }
    public get MessengerState(): MessengerState { return this.messengerState; }
    public get LoggedUserId(): string { return (this.currentSession == null ? "" : this.currentSession!.userId); }

    private setUserSession(s: UserSession, user: string) {
        this.currentSession = s;
        this.currentUsername = user;

        this.setMessengerState(MessengerState.Logged);
    }

    private setMessengerState(s: MessengerState) {
        this.messengerState = s;

        this.onMessengerStateChange.trigger(s);
    }
    
    public connect() {

        if (this.currentSession == null) {
            throw "invalid session";
        }

        //var protocol = "https:" ? "wss:" : "ws:";
        //var wsUri = protocol + "//" + document.location.hostname + ":" + document.location.port + "/ws/messaging";
        var wsUri = this.BaseWSAPIUrl + "ws/messaging";
        console.log("connecting to " + wsUri);
        this.ws = new WebSocket(wsUri);
        this.ws.onclose = (e) => this.onWsError(e);
        this.ws.onopen = (e) => this.onWsOpen(e);
        this.ws.onmessage = (e) => this.onWsMessage(e);
        this.ws.onerror = (e) => this.onWsError(e);
    }
    
    private sendlogin() {
        if (this.currentSession != null) {
            console.log("sending login to WS", this.currentSession);
            var obj: StreamMessage = new StreamMessage();
            obj.tokenId = this.currentSession!.id;
            obj.streamMessageType = StreamMessageType.Singin;

            this.send(JSON.stringify(obj));
        } else {
            console.log("Session is not valid");
            this.closesocket();
        }
    }

    private sendinfo() {
        var obj: StreamMessage = new StreamMessage();
        obj.streamMessageType = StreamMessageType.Info;

        this.send(JSON.stringify(obj));
    }
    
    private send(msg: any) {
        if (this.ws != null) {
            console.log("sending message: ", msg);
            this.ws.send(msg);
        }
    }

    private onWsOpen(evt: Event) {
        console.log("Connected to WS");

        this.sendlogin();
    }

    private onWsMessage(evt: MessageEvent) {
        console.log("onMessage: ", evt.data);

        let obj: StreamMessage = JSON.parse(evt.data);
        console.log("parsed message: ", obj);

        //if (obj.streamMessageType == StreamMessageType.Message) {
        if (obj.message != null) {
            this.onChatMessage(obj.message, true);
        }
    }
    
    private onWsError(evt: Event) {
        console.log("onError ");
        console.log(evt);       
    }

    private onWsClose(evt: Event) {
        console.log("Closed ");
        console.log(evt);
    }

    public debug() {
        console.log("this.currentSession", this.currentSession);
        console.log("this.currentUsername", this.currentUsername);
        console.log("Contacts", this.Contacts);
        console.log("MessengerState", this.MessengerState);
        console.log("MessageLog", this.MessageLog);
    }

    private closesocket() {
        if (this.ws != null) {
            console.log("closing ", this.ws);
            this.ws.close();
            this.ws = undefined;
        } else {
            console.log("already closed");
        }
    }

    public sendMessage(to: string, toType: DestinationType, body: string) {
        if (this.currentSession == null) {
            throw "invalid session";
        }

        var obj: Message = new Message();
        obj.fromUserId = this.currentSession.userId;
        obj.to = new Destination();
        obj.to.id = to;
        obj.to.type = toType;
        obj.body = body;

        var tmsg: TransmittedMessage = {
            body: body,
            type: MessageType.Text,
            to: {
                id: to,
                type: toType
            }
        };

        fetch(this.BaseAPIUrl + 'api/message', {
            method: 'post',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + this.currentSession.id
            },
            body: JSON.stringify(tmsg)
        }).then(response => {
            if (response.ok) {
                this.onChatMessage(obj, false);
            } else {
                throw response;
            }
        });
    }

    private onChatMessage(msg: Message, received: boolean) {

        var key: string = (received ? msg.fromUserId : msg.to.id);

        console.log("onChatmessage, key is ", key);

        if (this.MessageLog[key] == null) {
            this.MessageLog[key] = new ContactInfo();
        }
        
        this.MessageLog[key].messages.push(msg);

        // profile still not loaded? query it
        if (this.MessageLog[key].userProfile == null) {

            var existingContact = this.contacts[key];

            if (existingContact != null) {
                this.MessageLog[key].userProfile = existingContact;
            } else {
                this.getProfile(key).then(up => {
                    this.addContact(up);
                    this.MessageLog[key].userProfile = up;
                });
            }
        }
    }

    public addContact(up: UserProfile) {
        this.Contacts[up.id] = up;
        console.log("contacts: ", this.Contacts);
    }

    public login(username: string, pwd: string): Promise<UserSession> {
        this.resetState();

        var lr: LoginRequest = new LoginRequest();
        lr.username = username;
        lr.password = pwd;

        return fetch(this.BaseAPIUrl + 'api/user/login', {
            method: 'post',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(lr)
        }).then(response => {
            if (response.ok) {
                var p = response.json() as Promise<UserSession>;
                return p.then(up => {
                    this.setUserSession(up, username);
                    return up;
                });
            }
            throw response;
        });
    }

    private resetState() {
        this.closesocket();
        this.messengerState = MessengerState.Offline;
        this.messageLog = {};
        this.contacts = {};
        this.currentSession = undefined;
        this.currentUsername = undefined;
    }

    public logout() {
        this.resetState();
    }

    public createUser(userProfile: UserProfile): Promise<boolean> {

        if (this.MessengerState != MessengerState.Offline) {
            throw new TypeError("state is invalid, pls logoff");
        }
        
        if (!userProfile) {
            throw new TypeError("userProfile is required");
        }

        if (!userProfile.birthDate) {
            throw new TypeError("birthDate is required");
        }

        if (!userProfile.password) {
            throw new TypeError("password is required");
        }

        if (!userProfile.username) {
            throw new TypeError("username is required");
        }

        return fetch(this.BaseAPIUrl + 'api/user/register', {
            method: 'post',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userProfile)
        }).then(response => {
            if (response.ok)
                return true;
            throw response;
        });
    }

    public getProfile(userName: string): Promise<UserProfile> {
        return fetch(this.BaseAPIUrl + 'api/user/' + userName)
            .then(response => {
                if (response.ok)
                    return response.json() as Promise<UserProfile>;
                throw response;
            });
    }
}

export const Messenger = new MessengerAPI();

