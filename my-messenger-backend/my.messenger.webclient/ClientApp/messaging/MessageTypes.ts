export interface HashTable<T> {
    [key: string]: T;
}

export class ContactInfo {
    userProfile?: UserProfile;
    messages: Message[] = [];

    constructor() {
        this.userProfile = undefined;
    }
}

export class LoginRequest {
    username: string;
    password: string;
}

export class UserSession {
    id: string;
    createdAt: string;
    userId: string;
}

export class UserProfile {
    id: string;
    username: string;
    birthDate: string;
    password: string;
}

export class TransmittedMessage {
    to: Destination; 
    type: MessageType;
    body: string;
}

export class StreamMessage {
    streamMessageType: StreamMessageType;
    message: Message;
    tokenId: string;
}

export enum StreamMessageType {
    Singin,
    Message,
    Ping,
    Info,
}

export class Message {
    fromUserId: string;
    to: Destination;
    type: MessageType;
    body: string;
    sentAt: string;
}

export enum MessageType {
    Text
}

export class Destination {
    id: string;
    type: DestinationType;
}

export enum DestinationType {
    User
}

export enum MessengerState {
    Offline = 1,
    Logged = 2,
    Connected = 3,
    Streaming = 4
}