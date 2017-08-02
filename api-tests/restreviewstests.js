var Promise = require("bluebird");
var request = Promise.promisifyAll(require('supertest'));

var casual = require('casual');

request = request('http://localhost:8080/restreviews/api');

let aid = null;
let aid2 = null;
let pid = null;
let email = null;
let email2 = null;
let password = 'topsecret';
let counter = 0;

console.log("Get the list of accounts(guest)")
request
  .get('/account')
  .set('Accept', 'application/json')
  .expect(401)
  .endAsync().then(res => {
    counter++;
    console.log(res.body);
    console.log(`GET /account: ${res.status}\n\n`);
    console.log("Create an account(guest")
    return request
      .post('/account')
      .send({ email: casual.email, password: 'topsecret', role: 'customer' })
      .set('Accept', 'application/json')
      .expect(200)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    aid = res.body.accountId;
    email = res.body.email;
    console.log(`POST /account: ${res.status}\n\n`);
    console.log("Get own account")
    return request
      .get(`/account/${aid}`)
      .auth(email, password)
      .set('Accept', 'application/json')
      .expect(200)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`GET /account/${aid}: ${res.status}\n\n`);
    console.log("Get an account with no auth")
    return request
      .get(`/account/${aid}`)
      .set('Accept', 'application/json')
      .expect(401)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`GET /account/${aid}: ${res.status}\n\n`);
    console.log("Create an manager account")
    return request
      .post('/account')
      .send({ email: casual.username, password: 'topsecret', role: 'manager' })
      .set('Accept', 'application/json')
      .expect(403)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`POST /account ${res.status}\n\n`);
    console.log("Create another customer account")
    return request
      .post('/account')
      .send({ email: casual.username, password: 'topsecret', role: 'customer' })
      .set('Accept', 'application/json')
      .expect(200)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    aid2 = res.body.accountId;
    email2 = res.body.email;
    console.log(`POST /account ${res.status}\n\n`);
    console.log("Create yet another customer account with credentials existing")
    return request
      .post('/account')
      .send({ email: casual.username, password: 'topsecret', role: 'customer' })
      .auth(email, password)
      .set('Accept', 'application/json')
      .expect(200)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`POST /account ${res.status}\n\n`);
    console.log("Create yet another customer account with credentials existing")
    return request
      .post('/account')
      .send({ email: casual.username, password: 'topsecret', role: 'manager' })
      .auth(email, password)
      .set('Accept', 'application/json')
      .expect(403)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`POST /account ${res.status}\n\n`);
    console.log("Create yet another customer account with non-existing creds")
    return request
      .post('/account')
      .send({ email: casual.username, password: 'topsecret', role: 'customer' })
      .auth('lala@example.com', password)
      .set('Accept', 'application/json')
      .expect(401)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`POST /account ${res.status}\n\n`);
    console.log("Update an account with wrong existing creds")
    return request
      .put(`/account/${aid}`)
      .send({ email: casual.email, password: 'topsecret', role: 'customer' })
      .auth(email2, password)
      .set('Accept', 'application/json')
      .expect(401)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`PUT /account/${aid} ${res.status}\n\n`);
    console.log("Update an account with wrong non-existing creds")
    return request
      .put(`/account/${aid}`)
      .send({ email: casual.email, password: 'topsecret', role: 'customer' })
      .auth('lala@example.com', password)
      .set('Accept', 'application/json')
      .expect(401)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`PUT /account/${aid} ${res.status}\n\n`);
    console.log("Update an account with manager creds")
    return request
      .put(`/account/${aid}`)
      .send({ email: casual.email, password: 'topsecret', role: 'customer' })
      .auth('manager@example.com', password)
      .set('Accept', 'application/json')
      .expect(403)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`PUT /account/${aid} ${res.status}\n\n`);
    console.log("Update my own account")
    return request
      .put(`/account/${aid2}`)
      .send({ email: casual.email, password: 'topsecret', role: 'customer' })
      .auth(email2, password)
      .set('Accept', 'application/json')
      .expect(200)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    email2 = res.body.email;
    console.log(`PUT /account/${aid2} ${res.status}\n\n`);
    console.log("Update an account with no creds")
    return request
      .put(`/account/${aid2}`)
      .send({ email: casual.email, password: 'topsecret', role: 'customer' })
      .set('Accept', 'application/json')
      .expect(401)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`PUT /account/${aid2} ${res.status}\n\n`);
    console.log("Get the list of accounts(user)")
    return request
      .get(`/account`)
      .set('Accept', 'application/json')
      .auth(email2, password)
      .expect(200)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`GET /account ${res.status}\n\n`);
    console.log("Get the list of accounts(manager)")
    return request
      .get(`/account`)
      .set('Accept', 'application/json')
      .auth('manager@example.com', password)
      .expect(200)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`GET /account ${res.status}\n\n`);
    console.log("Update my own account (customer) to manager")
    return request
      .put(`/account/${aid2}`)
      .send({ email: casual.email, password: 'topsecret', role: 'manager' })
      .auth(email2, password)
      .set('Accept', 'application/json')
      .expect(403)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`PUT /account/${aid2} ${res.status}\n\n`);
    console.log("Update my own account (manager) to manager")
    return request
      .put(`/account/1`)
      .send({ email: 'manager@example.com', password: 'topsecret', role: 'manager' })
      .auth('manager@example.com', password)
      .set('Accept', 'application/json')
      .expect(200)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`PUT /account/1 ${res.status}\n\n`);
    console.log("manager can read another account")
    return request
      .get(`/account/${aid2}`)
      .auth('manager@example.com', password)
      .set('Accept', 'application/json')
      .expect(200)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`GET /account/${aid2} ${res.status}\n\n`);
    console.log("customer cannot read another account")
    return request
      .get(`/account/${aid2}`)
      .auth(email, password)
      .set('Accept', 'application/json')
      .expect(403)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`GET /account/${aid2} ${res.status}\n\n`);
    console.log("Customer cannot delete an account")
    return request
      .delete(`/account/${aid2}`)
      .auth(email, password)
      .set('Accept', 'application/json')
      .expect(403)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`DELETE /account/${aid2} ${res.status}\n\n`);
    console.log("Customer cannot delete his account")
    return request
      .delete(`/account/${aid2}`)
      .auth(email2, password)
      .set('Accept', 'application/json')
      .expect(403)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`DELETE /account/${aid2} ${res.status}\n\n`);
    console.log("Guest cannot delete his account")
    return request
      .delete(`/account/${aid2}`)
      .set('Accept', 'application/json')
      .expect(401)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`DELETE /account/${aid2} ${res.status}\n\n`); 
    console.log("Manager can delete an account")
    return request
      .delete(`/account/${aid2}`)
      .auth('manager@example.com', password)
      .set('Accept', 'application/json')
      .expect(200)
      .endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`DELETE /account/${aid2} ${res.status}\n\n`); 
//     console.log("Update an account with non-existing auth:")
//     return request
//       .put('/account/1')
//       .send({ username: casual.username, password: 'topsecret', email: casual.email, role: 'manager' })
//       .auth('kanny', 'topsecret')
//       .set('Accept', 'application/json')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`PUT /account/1 ${res.status}\n\n`);
//     console.log("Delete an account/guest")
//     return request
//       .delete('/account/1')
//       .set('Accept', 'application/json')
//       .expect(403)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`DELETE /account/1 ${res.status}\n\n`);
//     console.log("Delete an account with wrong existing auth")
//     return request
//       .delete('/account/1')
//       .set('Accept', 'application/json')
//       .auth('Niko_Nicolas', 'topsecret')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`DELETE /account/1 ${res.status}\n\n`);
//     console.log("Delete an account with non-existing auth")
//     return request
//       .delete('/account/1')
//       .set('Accept', 'application/json')
//       .auth('kanny', 'topsecret')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`DELETE /account/1 ${res.status}\n\n`);
//     console.log("Create an account/guest")
//     return request
//       .post('/account')
//       .send({ username: casual.username, password: 'topsecret', email: casual.email, role: 'manager' })
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`POST /account ${res.status}\n\n`);
//     console.log("Create an account with non-existing auth:")
//         aaid = res.body.accountId;
//     ausername = res.body.username;
//     return request
//       .post('/account')
//       .send({ username: casual.username, password: 'topsecret', email: casual.email, role: 'manager' })
//       .auth('manny', 'topsecret')
//       .set('Accept', 'application/json')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`POST /account ${res.status}\n\n`);
//     console.log("Get accounts with auth:")
//     return request
//       .get('/account')
//       .auth(ausername, apassword)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`GET /account ${res.status}\n\n`);
//     console.log("Get accounts with incorrect auth:")
//     return request
//       .get('/account')
//       .auth('bad', 'bad')
//       .set('Accept', 'application/json')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`GET /account ${res.status}\n\n`);
//     console.log("Create an account with correct auth:")
//     return request
//       .post('/account')
//       .send({ username: casual.username, password: 'topsecret', email: casual.email, role: 'manager' })
//       .auth(ausername, apassword)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`POST /account ${res.status}\n\n`);
//     console.log("Update an account with correct auth:")
//     return request
//       .put(`/account/${aaid}`)
//       .send({ username: casual.username, password: 'topsecret', email: casual.email, role: 'manager' })
//       .auth(ausername, apassword)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     ausername = res.body.username;
//     console.log(res.body);
//     console.log(`PUT /account/${aaid} ${res.status}\n\n`);
//     console.log("Update an account with incorrect auth")
//     return request
//       .put(`/account/${aaid}`)
//       .send({ username: casual.username, password: 'topsecret', email: casual.email, role: 'manager' })
//       .auth('manny', 'topsecret')
//       .set('Accept', 'application/json')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`PUT /account/${aaid} ${res.status}\n\n`);
//     console.log("Delete an account with incorrect auth:")
//     return request
//       .delete(`/account/${aaid}`)
//       .auth('danny', 'topsecret')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`DELETE /account/${aaid} ${res.status}\n\n`);
//     console.log("Delete an account with correct auth:")
//     return request
//       .delete(`/account/${aaid}`)
//       .auth(ausername, apassword)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`DELETE /account/${aaid} ${res.status}\n\n`);
//     console.log("Get an account with correct auth:")
//     ausername = 'Davon.Wolff';
//     aaid = 1;
//     return request
//       .get(`/account/${aaid}`)
//       .auth(ausername, apassword)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     aaid = res.body.accountId;
//     ausername = res.body.username;
//     console.log(res.body);
//     console.log(`GET /account/${aaid} ${res.status}\n\n`);
//     console.log("Get bookmarks of an account with correct auth")
//     return request
//       .get(`/account/${aaid}/bookmark`)
//       .auth(ausername, apassword)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`GET /account/${aaid}/bookmarks ${res.status}\n\n`);
//     console.log("Get a non-existent bookmark of an account with correct auth:")
//     return request
//       .get(`/account/${aaid}/bookmark/10`)
//       .auth(ausername, apassword)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`GET /account/${aaid}/bookmarks/10 ${res.status}\n\n`);
//     console.log("Get a existing bookmark/guest")
//     return request
//       .get(`/account/${aaid}/bookmark/10`)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`GET /account/${aaid}/bookmarks/10 ${res.status}\n\n`);
//     console.log("Get a existing bookmark of an account with guest")
//     return request
//       .get(`/account/${aaid}/bookmark/11`)
//       .set('Accept', 'application/json')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`GET /account/${aaid}/bookmarks/11 ${res.status}\n\n`);
//     console.log("Get bookmarks of an account with incorrect auth:")
//     return request
//       .get(`/account/${aaid}/bookmark`)
//       .auth(ausername, 'topsecret2')
//       .set('Accept', 'application/json')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`GET /account/${aaid}/bookmark ${res.status}\n\n`);
//     console.log("Get bookmarks of another account with correct auth:")
//     return request
//       .get('/account/2/bookmark')
//       .auth(ausername, apassword)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`GET /account/${aaid}/bookmarks ${res.status}\n\n`);
//     console.log("Get bookmarks of another account as guest:")
//     return request
//       .get('/account/2/bookmark')
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`GET /account/${aaid}/bookmarks ${res.status}\n\n`);
//     console.log("Get bookmarks of account as non-existing user")
//     return request
//       .get(`/account/${aaid}/bookmark`)
//       .auth('Jerde_Melyssa', 'topsecret')
//       .set('Accept', 'application/json')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`GET /account/${aaid}/bookmarks ${res.status}\n\n`);
//     console.log("Create a bookmark with incorrect auth:")
//     return request
//       .post(`/account/${aaid}/bookmark`)
//       .auth('Jerde_Melyssa', 'topsecret')
//       .send({ scope: 'public', url: 'http://example.com', keyword: 'cat'})
//       .set('Accept', 'application/json')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`POST /account/${aaid}/bookmarks ${res.status}\n\n`);
//     console.log("Create a private bookmark with correct auth:")
//     return request
//       .post(`/account/${aaid}/bookmark`)
//       .auth(ausername, apassword)
//       .send({ scope: 'private', url: 'http://example.com', keyword: 'cat'})
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`POST /account/${aaid}/bookmarks ${res.status}\n\n`);
//     console.log("Create a public bookmark with correct auth:")
//     return request
//       .post(`/account/${aaid}/bookmark`)
//       .auth(ausername, apassword)
//       .send({ scope: 'public', url: 'http://example.com', keyword: 'cat'})
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     abid = res.body.bookmarkId;
//     console.log(res.body);
//     console.log(`POST /account/${aaid}/bookmarks ${res.status}\n\n`);
//     console.log("Update a bookmark with correct auth:")
//     return request
//       .put(`/account/${aaid}/bookmark/${abid}`)
//       .auth(ausername, apassword)
//       .send({ scope: 'private', url: 'http://example.com', keyword: 'cat'})
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     abid = res.body.bookmarkId;
//     console.log(res.body);
//     console.log(`PUT /account/${aaid}/bookmarks/${abid} ${res.status}\n\n`);
//     console.log("Create a public bookmark with correct auth:")
//     return request
//       .post(`/account/${aaid}/bookmark`)
//       .auth(ausername, apassword)
//       .send({ scope: 'public', url: 'http://example.com/1', keyword: 'cat'})
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`POST /account/${aaid}/bookmarks ${res.status}\n\n`);
//     console.log("Create a public bookmark with correct auth:")
//     return request
//       .post(`/account/${aaid}/bookmark`)
//       .auth(ausername, apassword)
//       .send({ scope: 'private', url: 'http://example.com/2', keyworkd: 'cat'})
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     let bid = res.body.bookmarkId;
//     console.log(res.body);
//     console.log(`POST /account/${aaid}/bookmarks ${res.status}\n\n`);
//     console.log("Delete a bookmark with correct auth:")
//     return request
//       .delete(`/account/${aaid}/bookmark/${bid}`)
//       .auth(ausername, apassword)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     let bid = res.body.bookmarkId;
//     console.log(res.body);
//     console.log(`DELETE /account/${aaid}/bookmark/${bid} ${res.status}\n\n`);
//     console.log("Get bookmarks with existing owner's auth")
//     return request
//       .get(`/account/${aaid}/bookmark`)
//       .auth(ausername, apassword)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`GET /account/${aaid}/bookmarks ${res.status}\n\n`);
//     console.log("Update bookmark with existing incorrect auth")
//     return request
//       .put(`/account/${aaid}/bookmark/${abid}`)
//       .auth('Henriette_Dicki', 'topsecret')
//       .send({ scope: 'private', url: 'http://example.com', keyword: 'cat'})
//       .set('Accept', 'application/json')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`PUT /account/${aaid}/bookmark/${abid} ${res.status}\n\n`);
//     console.log("Create bookmakr non-existing auth")
//     return request
//       .post(`/account/${aaid}/bookmark`)
//       .auth('Henriette_Dicki', 'topsecret')
//       .send({ scope: 'public', url: 'http://example.com/1'})
//       .set('Accept', 'application/json')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`POST /account/${aaid}/bookmarks ${res.status}\n\n`);
//     console.log("Delete bookmark non-existing auth");
//     return request
//       .delete(`/account/${aaid}/bookmark/${abid}`)
//       .auth('Henriette_Dicki', 'topsecret')
//       .set('Accept', 'application/json')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`DELETE /account/${aaid}/bookmark/${abid} ${res.status}\n\n`);
//     console.log("Search existing auth")
//     return request
//       .get('/AlgobookmarkSearch')
//       .query({searchBookmarkKeyword: true, searchKeyword: 'cat'})
//       .auth(ausername, apassword)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body.linklist.length);
//     console.log(`GET /AlgobookmarkSearch auth ${res.status}\n\n`);
//     console.log("Search non-existing auth")
//     return request
//       .get('/AlgobookmarkSearch')
//       .query({searchBookmarkKeyword: true, searchKeyword: 'cat'})
//       .auth('Jerde_Melyssa', 'topsecrets')
//       .set('Accept', 'application/json')
//       .expect(401)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body);
//     console.log(`GET /AlgobookmarkSearch failed-auth ${res.status}\n\n`);
//     console.log("Search guest")
//     return request
//       .get('/AlgobookmarkSearch')
//       .query({searchBookmarkKeyword: true, searchKeyword: 'cat'})
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body.linklist.length);
//     console.log(`GET /AlgobookmarkSearch no-auth ${res.status}\n\n`);
//     console.log("Search existing auth, no results")
//     return request
//       .get('/AlgobookmarkSearch')
//       .query({searchBookmarkKeyword: true, searchKeyword: 'dog'})
//       .auth(ausername, apassword)
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body)
//     console.log(`GET /AlgobookmarkSearch auth-no-results ${res.status}\n\n`);
//     console.log("Search guest, no results")
//     return request
//       .get('/AlgobookmarkSearch')
//       .query({searchBookmarkKeyword: true, searchKeyword: 'dog'})
//       .set('Accept', 'application/json')
//       .expect(200)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body)
//     console.log(`GET /AlgobookmarkSearch no-auth-no-results ${res.status}\n\n`);
//     console.log("Search guest, no keyword")
//     return request
//       .get('/AlgobookmarkSearch')
//       .query({searchBookmarkKeyword: true})
//       .set('Accept', 'application/json')
//       .expect(500)
//       .endAsync();
//   }).then(res => {
//     console.log(res.body)
//     console.log(`GET /AlgobookmarkSearch empty-keyword ${res.status}\n\n`);
    console.log("Tests completed: " + counter)
  }).catch((err, result) => {
    console.log("Tests completed: " + counter)
    console.log(err);
  });