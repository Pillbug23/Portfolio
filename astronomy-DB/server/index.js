const express = require("express");
const bodyParser = require("body-parser");
const mongoose = require("mongoose");
const url = "mongodb://127.0.0.1:27017/node-mongo-hw"; // change this as needed
const Datastore = require('nedb');
const axios = require('axios');

const db = mongoose.connection;
db.once("open", (_) => {
  console.log("Database connected:", url);
});

db.on("error", (err) => {
  console.error("connection error:", err);
});

const app = express();
const database = new Datastore('database.db');
database.loadDatabase();

const cors = require('cors')
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(cors())

var port = process.env.PORT || 8080;

var router = express.Router();
datastore = {};

// The method of the root url. Be friendly and welcome our user :)
router.get("/", function (req, res) {
  res.json({ message: "Welcome to the APOD app." });
});

router.get("/favorite", function (req, res) {
  database.find({ }, function (err, docs) {
    var x = docs;
    res.send(x);
  });
});


router.post("/add", function (req, res) {
  // TODO:
  console.log("Favorited Picture");
  const store = req.body;
  console.log(req.body);
  database.insert(store);
});

router.post("/delete", function (req, res) {
  // TODO:
  console.log("Favorited Picture Deleted");
  console.log(req.body);
  const store = req.body;
  database.remove(store);
});

app.use("/api", router); // API Root url at: http://localhost:8080/api

app.listen(port);
console.log("Server listening on port " + port);
