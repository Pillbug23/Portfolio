const express = require('express')
const port = 3001
const cors = require('cors');


const post1 = {id: 0, imgid: 300, color:"rgb(59, 110, 0)", comments:['omg this is such a cool color', 'COLOR VIBEZZZ', 'first like']};
const post2 = {id: 1, imgid: 307, color:"rgb(58, 0, 97)", comments:[]};
const post3 = {id: 2, imgid: 305, color:"rgb(134, 5, 5)", comments:['This is #lame', 'Dont listen to the haterzzz, this is really cool']};
const post4 = {id: 3, imgid: 304, color:"rgb(134, 5, 5)", comments:['Hey why arent you answeing my calls', 'really like your vibe, DM  to be a ambassador', 'spam 1', 'spam 2']};
const post5 = {id: 4, imgid: 306, color:"rgb(58, 0, 97)", comments:['I love gravy', 'Hawaii is awesome', 'I ran out of fake comments']};
let data= {0:post1, 1:post2, 2:post3, 3:post4, 4: post5}


const app = express();
app.use(cors());
app.use(express.json());


app.listen(port, () => {
  console.log(`Block Server listening at http://localhost:${port}`)
})

app.get('/posts', (req, res) => {
  res.send(data)
}
)

app.post('/newpost', (req, res) => {
  newpost = {id:req.body.id, color:req.body.col, comments:req.body.com, imgid: req.body.imgid}
  data[req.body.id] = newpost
  res.send('success!!!')
}
)

app.post('/updatecomment', (req, res) => {
  selectblock = data[req.body.id]
  selectblock.comments = selectblock.comments.concat(req.body.newcomment)
  res.send('success!!!')
})

app.get('/comments', (req, res) => {
  selectedblock = data[req.query.id]
  res.send(selectedblock.comments)
}
)