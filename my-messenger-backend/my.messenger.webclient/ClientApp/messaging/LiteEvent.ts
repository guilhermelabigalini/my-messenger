//https://gist.github.com/JasonKleban/50cee44960c225ac1993c922563aa540
//https://www.wintellect.com/building-a-javascript-event-aggregator-using-typescript/
export interface ILiteEvent<T> {
    on(handler: { (data?: T): void; }): number;
    off(handlerId: number): void;
}

class Handler {
    id: number;
    callback: { (data?: T): void; };
}

export class LiteEvent<T> implements ILiteEvent<T> {
    private _nextId: number;
    private handlers: Handler[] = [];

    constructor() {
        this._nextId = 0;
    }

    public on(handler: (data?: T) => void): number {
        this._nextId++;
        var h: Handler = {
            id: this._nextId,
            callback: handler
        };
        this.handlers.push(h);
        return this._nextId;
    }

    //public off(handler: { (data?: T): void }): void {
    public off(handlerId: number): void {
        this.handlers = this.handlers.filter(h => h.id !== handlerId);
    }
    
    public trigger(data?: T) {
        this.handlers.forEach(h => h.callback(data));
    }

    public expose(): ILiteEvent<T> {
        return this;
    }
}