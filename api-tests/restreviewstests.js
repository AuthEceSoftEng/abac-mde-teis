var Promise = require("bluebird");
var request = Promise.promisifyAll(require('supertest'));

var casual = require('casual');

request = request('http://localhost:8080/restreviews/api');

let aid = null; //customer
let aid2 = null; //to be deleted
let aid3 = null; //customer
let pid = null; //draft
let pid2 = null; //available
let pid3 = null; //to be deleted
let oid = null; //to be deleted
let rid = null;
let rid2 = null;
let email = null;
let email2 = null;
let email3 = null;
let managerEmail = 'manager@example.com'
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
  aid3 = res.body.accountId;
  email3 = res.body.email;
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
      .expect(403)
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
	console.log(`PRODUCT TESTING BEGINS HERE ----------------------`);
	console.log(`Create a product as guest`)
	return request
    	.post('/multiproductManager/account/1/product')
		.send({ cost: 10.5, description: 'The best product to date!', status: 'draft', title: 'Product1' })
		.set('Accept', 'application/json')
		.expect(401)
		.endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`/multiproductManager/account/1/product ${res.status}\n\n`); 
	
	console.log(`Create a product as customer`)
	return request
  		.post('/multiproductManager/account/1/product')
		.send({ cost: 10.5, description: 'The best product to date!', status: 'draft', title: 'Product1' })
		.auth(email, password)
		.set('Accept', 'application/json')
		.expect(403)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproductManager/account/1/product ${res.status}\n\n`); 	
	
	console.log(`Create a draft product as manager`)
	return request
  		.post('/multiproductManager/account/1/product')
		.send({ cost: 10.5, description: 'The best product to date!', status: 'draft', title: 'Product1' })
		.auth(managerEmail, password)
		.set('Accept', 'application/json')
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		pid = res.body.productId;
		console.log(res.body);
		console.log(`/multiproductManager/account/1/product ${res.status} create product with id ${pid}\n\n`); 	
	
	console.log(`Create a 2nd draft product as manager`)
	return request
  		.post('/multiproductManager/account/1/product')
		.send({ cost: 12.5, description: 'The 2nd best product to date!', status: 'draft', title: 'Product2' })
		.auth(managerEmail, password)
		.set('Accept', 'application/json')
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		pid2 = res.body.productId;
		console.log(res.body);
		console.log(`/multiproductManager/account/1/product ${res.status} create product with id ${pid2}\n\n`); 
		
	console.log(`Create a 3rd draft product as manager`)
	return request
  		.post('/multiproductManager/account/1/product')
		.send({ cost: 12.5, description: 'The 3rd best product to date!', status: 'draft', title: 'Product3' })
		.auth(managerEmail, password)
		.set('Accept', 'application/json')
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		pid3 = res.body.productId;
		console.log(res.body);
		console.log(`/multiproductManager/account/1/product ${res.status} create product with id ${pid3}\n\n`); 	
	
	console.log(`Get a product as guest`)
	return request
  		.get(`/multiproduct/account/1/product/${pid}`)
		.set('Accept', 'application/json')
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid} ${res.status}\n\n`); 
	
	console.log(`Get a draft product as customer`)
	return request
  		.get(`/multiproduct/account/1/product/${pid}`)
		.set('Accept', 'application/json')
		.auth(email, password)
		.expect(403)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid} ${res.status}\n\n`); 	
		
	console.log(`Get a draft product as manager`)
	return request
  		.get(`/multiproduct/account/1/product/${pid}`)
		.set('Accept', 'application/json')
		.auth(managerEmail, password)
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid} ${res.status}\n\n`); 
		
	console.log(`Update a draft product as guest`)
	return request
  		.put(`/multiproduct/account/1/product/${pid2}`)
		.send({ cost: 12.5, description: 'The 2nd best product to date!', status: 'available', title: 'Product12' })
		.set('Accept', 'application/json')
		.expect(401)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid} ${res.status} \n\n`); 	
		
	console.log(`Update a draft product as customer`)
	return request
  		.put(`/multiproduct/account/1/product/${pid2}`)
		.send({ cost: 12.5, description: 'The 2nd best product to date!', status: 'available', title: 'Product12' })
		.set('Accept', 'application/json')
		.auth(email, password)
		.expect(403)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid} ${res.status} \n\n`); 	
		
	console.log(`Update a draft product as manager`)
	return request
  		.put(`/multiproduct/account/1/product/${pid2}`)
		.send({ cost: 12.5, description: 'The 2nd best product to date!', status: 'available', title: 'Product2' })
		.set('Accept', 'application/json')
		.auth(managerEmail, password)
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid2} ${res.status} \n\n`); 		
				
	console.log(`Update an available product as guest`)
	return request
  		.put(`/multiproduct/account/1/product/${pid2}`)
		.send({ cost: 12.5, description: 'The 2nd best product to date!', status: 'available', title: 'Product12' })
		.set('Accept', 'application/json')
		.expect(401)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid} ${res.status} \n\n`); 	
		
	console.log(`Update an available product as customer`)
	return request
  		.put(`/multiproduct/account/1/product/${pid2}`)
		.send({ cost: 12.5, description: 'The 2nd best product to date!', status: 'available', title: 'Product12' })
		.set('Accept', 'application/json')
		.auth(email, password)
		.expect(403)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid} ${res.status} \n\n`); 	
		
	console.log(`Update an available product as manager`)
	return request
  		.put(`/multiproduct/account/1/product/${pid2}`)
		.send({ cost: 12.5, description: 'The 2nd best product to date!', status: 'available', title: 'Product2' })
		.set('Accept', 'application/json')
		.auth(managerEmail, password)
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid2} ${res.status} \n\n`); 	
				
	console.log(`Get an available product as guest`)
	return request
  		.get(`/multiproduct/account/1/product/${pid2}`)
		.set('Accept', 'application/json')
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid2} ${res.status}\n\n`); 
	
	console.log(`Get an available product as customer`)
	return request
  		.get(`/multiproduct/account/1/product/${pid2}`)
		.set('Accept', 'application/json')
		.auth(email, password)
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid2} ${res.status}\n\n`); 	
		
	console.log(`Get an available product as manager`)
	return request
  		.get(`/multiproduct/account/1/product/${pid2}`)
		.set('Accept', 'application/json')
		.auth(managerEmail, password)
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid2} ${res.status}\n\n`);		
		
		
	console.log(`Get list of products as guest`)
	return request
  		.get(`/multiproductManager/account/1/product/`)
		.set('Accept', 'application/json')
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproductManager/account/1/product/ ${res.status} \n\n`); 
		
	console.log(`Get list of products as customer`)
	return request
  		.get(`/multiproductManager/account/1/product/`)
		.set('Accept', 'application/json')
		.auth(email, password)
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproductManager/account/1/product/ ${res.status} \n\n`); 
		
	console.log(`Get list of products as manager`)
	return request
  		.get(`/multiproductManager/account/1/product/`)
		.set('Accept', 'application/json')
		.auth(managerEmail, password)
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproductManager/account/1/product/ ${res.status} \n\n`); 
		
	console.log(`Delete product as guest`)
	return request
  		.delete(`/multiproduct/account/1/product/${pid3}`)
		.set('Accept', 'application/json')
		.expect(401)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid3} ${res.status} \n\n`);
		
	console.log(`Delete product as customer`)
	return request
  		.delete(`/multiproduct/account/1/product/${pid3}`)
		.set('Accept', 'application/json')
		.auth(email, password)
		.expect(403)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid3} ${res.status} \n\n`);
		
	console.log(`Delete product as manager`)
	return request
  		.delete(`/multiproduct/account/1/product/${pid3}`)
		.set('Accept', 'application/json')
		.auth(managerEmail, password)
		.expect(200)
		.endAsync();
	}).then(res => {
		counter++;
		console.log(res.body);
		console.log(`/multiproduct/account/1/product/${pid3} ${res.status} \n\n`);
		
		console.log('FROM HERE BEGINS ORDER TESTING\n\n');
		
	console.log(`Create an order as guest`)
	return request
    	.post('/account/1/order')
		.send({ discountCoupon: 'none', orderDate: '2017-07-20 20:24:33.2'})
		.set('Accept', 'application/json')
		.expect(401)
		.endAsync();
  }).then(res => {
    counter++;
    console.log(res.body);
    console.log(`/account/1/order ${res.status}\n\n`); 
	
console.log(`Create an order as manager`)
return request
  	.post('/account/1/order')
	.send({ discountCoupon: 'none', orderDate: '2017-07-20 20:24:33.2'})
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/1/order ${res.status}\n\n`); 
  
console.log(`Create an order as customer`)
return request
  	.post(`/account/${aid}/order`)
	.send({ discountCoupon: 'none', orderDate: '2017-07-20 20:24:33.2'})
	.set('Accept', 'application/json')
	.auth(email, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  oid = res.body.orderId;
  console.log(res.body);
  console.log(`/account/${aid}/order ${res.status}\n\n`); 
  
console.log(`Update an order as guest`)
return request
  	.put(`/account/${aid}/order/${oid}`)
	.send({ discountCoupon: 'none', orderDate: '2017-07-20 20:24:33.2'})
	.set('Accept', 'application/json')
	.expect(401)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/${aid}/order/${oid} ${res.status}\n\n`); 
  
console.log(`Update an order as manager`)
return request
  	.put(`/account/${aid}/order/${oid}`)
	.send({ discountCoupon: 'none', orderDate: '2017-07-20 20:24:33.2'})
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/${aid}/order/${oid} ${res.status}\n\n`); 
  
console.log(`Update an order as customer who is not owner`)
return request
  	.put(`/account/${aid}/order/${oid}`)
	.send({ discountCoupon: 'none', orderDate: '2017-07-20 20:24:33.2'})
	.set('Accept', 'application/json')
	.auth(email3, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/${aid}/order/${oid} ${res.status}\n\n`); 
  
console.log(`Update an order as owner`)
return request
  	.put(`/account/${aid}/order/${oid}`)
	.send({ discountCoupon: 'souperCoupon', orderDate: '2017-07-20 20:24:33.2'})
	.set('Accept', 'application/json')
	.auth(email, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/${aid}/order/${oid} ${res.status}\n\n`); 

  
console.log(`Get an order as guest`)
return request
  	.get(`/account/${aid}/order/${oid}`)
	.set('Accept', 'application/json')
	.expect(401)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/${aid}/order/${oid} ${res.status}\n\n`); 
  
console.log(`Get an order as customer who is not owner`)
return request
  	.get(`/account/${aid}/order/${oid}`)
	.set('Accept', 'application/json')
	.auth(email3, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/${aid}/order/${oid} ${res.status}\n\n`); 
  
console.log(`Get an order as customer who is the owner`)
return request
  	.get(`/account/${aid}/order/${oid}`)
	.set('Accept', 'application/json')
	.auth(email, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/${aid}/order/${oid} ${res.status}\n\n`); 
  
console.log(`Get an order as manager`)
return request
  	.get(`/account/${aid}/order/${oid}`)
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/${aid}/order/${oid} ${res.status}\n\n`); 
  
console.log(`Delete an order as guest`)
return request
  	.delete(`/account/${aid}/order/${oid}`)
	.set('Accept', 'application/json')
	.expect(401)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/${aid}/order/${oid} ${res.status}\n\n`); 
 
  
console.log(`Delete an order as customer who is not owner`)
return request
  	.delete(`/account/${aid}/order/${oid}`)
	.set('Accept', 'application/json')
	.auth(email3, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/${aid}/order/${oid} ${res.status}\n\n`); 
  
console.log(`Delete an order as owner`)
return request
  	.delete(`/account/${aid}/order/${oid}`)
	.set('Accept', 'application/json')
	.auth(email, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/${aid}/order/${oid} ${res.status}\n\n`); 
  
console.log(`Delete an order as manager`)
return request
  	.delete(`/account/${aid}/order/${oid}`)
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/account/${aid}/order/${oid} ${res.status}\n\n`); 
	
	
console.log('FROM HERE BEGINS REVIEW TESTING\n\n');
	
console.log(`Create a review as guest on a draft product`)
return request
  	.post(`/product/${pid}/review`)
	.send({ title: 'Solid choice', description: 'I have been owner for 4 weeks without issues so far!'})
	.set('Accept', 'application/json')
	.expect(401)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid}/review ${res.status}\n\n`); 
  
console.log(`Create a review as customer on a draft product`)
return request
  	.post(`/product/${pid}/review`)
	.send({ title: 'Solid choice', description: 'I have been owner for 4 weeks without issues so far!'})
	.set('Accept', 'application/json')
	.auth(email, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid}/review ${res.status}\n\n`); 
  
console.log(`Create a review as manager on a draft product`)
return request
  	.post(`/product/${pid}/review`)
	.send({ title: 'Solid choice', description: 'I have been owner for 4 weeks without issues so far!'})
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid}/review ${res.status}\n\n`); 
  
console.log(`Update a product as manager to be available for reviews`)
return request
	.put(`/multiproduct/account/1/product/${pid}`)
	.send({ cost: 10, description: 'The best product to date!', status: 'available', title: 'Product1' })
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(200)
	.endAsync();
}).then(res => {
	counter++;
	console.log(res.body);
	console.log(`/multiproduct/account/1/product/${pid} ${res.status} \n\n`); 
  
console.log(`Create a review as guest on an available product`)
return request
  	.post(`/product/${pid2}/review`)
	.send({ title: 'Solid choice', description: 'I have been owner for 4 weeks without issues so far!'})
	.set('Accept', 'application/json')
	.expect(401)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review ${res.status}\n\n`); 
  
console.log(`Create a review as manager on an available product`)
return request
  	.post(`/product/${pid2}/review`)
	.send({ title: 'Solid choice', description: 'I have been owner for 4 weeks without issues so far!'})
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review ${res.status}\n\n`); 
  
console.log(`Create a review as customer on an available product`)
return request
  	.post(`/product/${pid2}/review`)
	.send({ title: 'Solid choice', description: 'I have been owner for 4 weeks without issues so far!'})
	.set('Accept', 'application/json')
	.auth(email, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  rid = res.body.reviewId;
  console.log(res.body);
  console.log(`/product/${pid2}/review ${res.status}\n\n`); 
  
  
console.log(`Create a 2nd review as customer on an available product`)
return request
  	.post(`/product/${pid}/review`)
	.send({ title: 'Bad choice', description: 'I have been owner for 4 weeks, full of issues so far!'})
	.set('Accept', 'application/json')
	.auth(email, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  rid2 = res.body.reviewId;
  console.log(res.body);
  console.log(`/product/${pid}/review ${res.status}\n\n`); 
  
console.log(`Update a product as manager to be draft and not available for reviews`)
return request
	.put(`/multiproduct/account/1/product/${pid}`)
	.send({ cost: 10, description: 'The best product to date!', status: 'draft', title: 'Product1' })
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(200)
	.endAsync();
}).then(res => {
	counter++;
	console.log(res.body);
	console.log(`/multiproduct/account/1/product/${pid} ${res.status} \n\n`); 
  
console.log(`Get a review as guest on a draft product`)
return request
  	.get(`/product/${pid}/review/${rid2}`)
	.set('Accept', 'application/json')
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid}/review/${rid2} ${res.status}\n\n`); 
  
console.log(`Get a review as customer who is not owner of the review on a draft product`)
return request
  	.get(`/product/${pid}/review/${rid2}`)
	.set('Accept', 'application/json')
	.auth(email3, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid}/review/${rid2} ${res.status}\n\n`); 
  
console.log(`Get a review as owner of the review on a draft product`)
return request
  	.get(`/product/${pid}/review/${rid2}`)
	.set('Accept', 'application/json')
	.auth(email, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid}/review/${rid2} ${res.status}\n\n`); 
  
console.log(`Get a review as manager on a draft product`)
return request
  	.get(`/product/${pid}/review/${rid2}`)
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid}/review/${rid2} ${res.status}\n\n`); 
  
console.log(`Get a review as guest on an available product`)
return request
  	.get(`/product/${pid2}/review/${rid}`)
	.set('Accept', 'application/json')
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review/${rid} ${res.status}\n\n`); 
  
console.log(`Get a review as customer who is not owner of the review on an available product`)
return request
  	.get(`/product/${pid2}/review/${rid}`)
	.set('Accept', 'application/json')
	.auth(email3, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review/${rid} ${res.status}\n\n`); 
  
console.log(`Get a review as owner of the review on an available product`)
return request
  	.get(`/product/${pid2}/review/${rid}`)
	.set('Accept', 'application/json')
	.auth(email, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review/${rid} ${res.status}\n\n`); 
  
console.log(`Get a review as manager on an available product`)
return request
  	.get(`/product/${pid2}/review/${rid}`)
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review/${rid} ${res.status}\n\n`); 
    
console.log(`Update a review as guest on a draft product`)
return request
  	.put(`/product/${pid}/review/${rid2}`)
	.send({ title: 'Bad choice', description: 'I have been owner for 4 weeks, full of issues so far!'})
	.set('Accept', 'application/json')
	.expect(401)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid}/review/${rid2} ${res.status}\n\n`); 
  
console.log(`Update a review as customer who is not owner of the review on a draft product`)
return request
  	.put(`/product/${pid}/review/${rid2}`)
	.send({ title: 'Bad choice', description: 'I have been owner for 4 weeks, full of issues so far!'})
	.set('Accept', 'application/json')
	.auth(email3, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid}/review/${rid2} ${res.status}\n\n`); 
  
console.log(`Update a review as owner of the review on a draft product`)
return request
  	.put(`/product/${pid}/review/${rid2}`)
	.send({ title: 'Bad choice', description: 'I have been owner for 4 weeks, full of issues so far!'})
	.set('Accept', 'application/json')
	.auth(email, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid}/review/${rid2} ${res.status}\n\n`); 
  
console.log(`Update a review as manager on a draft product`)
return request
  	.put(`/product/${pid}/review/${rid2}`)
	.send({ title: 'Bad choice', description: 'I have been owner for 4 weeks, full of issues so far!'})
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid}/review/${rid2} ${res.status}\n\n`); 
  
console.log(`Update a review as guest on an available product`)
return request
  	.put(`/product/${pid2}/review/${rid}`)
	.send({ title: 'Bad choice', description: 'I have been owner for 4 weeks, full of issues so far!'})
	.set('Accept', 'application/json')
	.expect(401)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review/${rid} ${res.status}\n\n`); 
  
console.log(`Update a review as customer who is not owner of the review on an available product`)
return request
  	.put(`/product/${pid2}/review/${rid}`)
	.send({ title: 'Bad choice', description: 'I have been owner for 4 weeks, full of issues so far!'})
	.set('Accept', 'application/json')
	.auth(email3, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review/${rid} ${res.status}\n\n`); 
  
console.log(`Update a review as owner of the review on an available product`)
return request
  	.put(`/product/${pid2}/review/${rid}`)
	.send({ title: 'Bad choice', description: 'I have been owner for 4 weeks, full of issues so far!'})
	.set('Accept', 'application/json')
	.auth(email, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review/${rid} ${res.status}\n\n`); 
  
console.log(`Update a review as manager on an available product`)
return request
  	.put(`/product/${pid2}/review/${rid}`)
	.send({ title: 'Bad choice', description: 'I have been owner for 4 weeks, full of issues so far!'})
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review/${rid} ${res.status}\n\n`); 
    
console.log(`Delete a review as guest on an available product`)
return request
  	.delete(`/product/${pid2}/review/${rid}`)
	.set('Accept', 'application/json')
	.expect(401)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review/${rid} ${res.status}\n\n`); 
  
console.log(`Delete a review as customer who is not owner of the review on an available product`)
return request
  	.delete(`/product/${pid2}/review/${rid}`)
	.set('Accept', 'application/json')
	.auth(email3, password)
	.expect(403)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review/${rid} ${res.status}\n\n`); 
  
console.log(`Delete a review as owner of the review on an available product`)
return request
  	.delete(`/product/${pid}/review/${rid2}`)
	.set('Accept', 'application/json')
	.auth(email, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid}/review/${rid2} ${res.status}\n\n`); 
  
console.log(`Delete a review as manager on an available product`)
return request
  	.delete(`/product/${pid2}/review/${rid}`)
	.set('Accept', 'application/json')
	.auth(managerEmail, password)
	.expect(200)
	.endAsync();
}).then(res => {
  counter++;
  console.log(res.body);
  console.log(`/product/${pid2}/review/${rid} ${res.status}\n\n`); 
	
    console.log("Tests completed: " + counter)
  }).catch((err, result) => {
    console.log("Tests completed: " + counter)
    console.log(err);
  });