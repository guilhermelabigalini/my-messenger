import * as React from 'react';
import { Route } from 'react-router-dom';
import { Layout } from './components/Layout';
import { Home } from './components/Home';
import { SearchUser } from './components/SearchUser';
import { RegisterUser } from './components/RegisterUser';
import { Login } from './components/Login';
import { Contacts } from './components/Contacts';
import { DevPanel } from './components/DevPanel';

export const routes = <Layout>
    <Route exact path='/' component={ Home } />
    <Route path='/searchuser' component={SearchUser} />
    <Route path='/registeruser' component={RegisterUser} />
    <Route path='/login' component={Login} />
    <Route path='/contacts' component={Contacts} />
    <Route path='/dev' component={DevPanel} />
</Layout>;
