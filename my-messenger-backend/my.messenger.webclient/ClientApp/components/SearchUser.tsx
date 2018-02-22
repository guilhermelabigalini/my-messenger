import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { UserProfile } from '../messaging/MessageTypes';
import * as Messenger from '../messaging/Messenger';
let MessengerAPI = Messenger.Messenger;

interface SearchUserState {
    searchString: string;
    isSearching: boolean;
    userProfile?: UserProfile;
    isNotFound: boolean;
}

export class SearchUser extends React.Component<RouteComponentProps<{}>, SearchUserState> {
    constructor() {
        super();
        this.state = { searchString: "", isSearching: false, isNotFound: false };
    }

    public render() {
        return <div>
            <h1>Search user</h1>

            <div className="form-group">
                <label htmlFor="searchBox">Name:</label>
                <input id="searchBox" className="form-control"type="text" name="name" value={this.state.searchString} onChange={(e) => { this.handleChangeText(e) }} />
            </div>
            <button className="btn btn-primary" onClick={() => { this.doSearch() }}>Search</button>
            <div>
            {this.state.isSearching &&
                <text>Searching...</text>
            }
            {this.state.userProfile != undefined &&
                <div>
                <table className='table'>
                    <thead>
                        <tr>
                            <th>id</th>
                            <th>Username</th>
                            <th>Birth date</th>
                            <th>Add</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>{this.state.userProfile.id}</td>
                            <td>{this.state.userProfile.username}</td>
                            <td>{this.state.userProfile.birthDate}</td>
                            <td><button className="btn btn-primary" onClick={() => { this.addContact() }}>Add</button></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            }
            {this.state.isNotFound &&
                <text>User not found!</text>
            }
            </div>
        </div>;
    }

    handleChangeText(event: React.FormEvent<HTMLInputElement>) {
        this.setState({ searchString: event.currentTarget.value });
    }

    addContact() {
        console.log("adding contact ", this.state);
        MessengerAPI.addContact(this.state.userProfile!);
        alert("user " + this.state.userProfile!.username + " added to the contact");
    }

    doSearch() {
        console.log("Searching for " + this.state.searchString);

        this.setState({ isSearching: true, isNotFound: false  });

        //fetch('http://localhost:8080/api/user/' + this.state.searchString)
        //    //.then(this.handleErrors)
        //    .then(response => {
        //        if (response.ok)
        //            return response.json() as Promise<UserProfile>;
        //        throw response;
        //    })
        MessengerAPI.getProfile(this.state.searchString)
            .then(data => {
                console.log("got profile", data);
                this.setState({ userProfile: data, isSearching: false });
            })
            .catch(e => {
                console.error("unable to search: ", e);
                this.setState({ userProfile: undefined, isSearching: false, isNotFound: true });
            });
    }
}
