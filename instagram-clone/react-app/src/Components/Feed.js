import axios from 'axios';
import React from 'react';
import Block from './Block';


class Feed extends React.Component {

    state = {
      showopt: false,
      blocks: [],
      var: ''
    }

    componentDidMount() {
      axios
      .get('http://localhost:3001/posts')
      .then((response) => {
        var returnedblocks = []
        for (const element in response.data) {
          const selectedblock = response.data[element]
          returnedblocks = returnedblocks.concat(<Block key = {selectedblock.id} imgid = {selectedblock.imgid} id={selectedblock.id} color={selectedblock.color} comments={selectedblock.comments}></Block>)
        }
        this.setState({blocks: returnedblocks})
      })
      .catch(error => { console.error('There was an error!', error); });
    }

    Updatefeed() {
    axios.get('http://localhost:3001/posts')
    .then((response) => {
      var returnedblocks = []
      for (const element in response.data) {
        const selectedblock = response.data[element]
        returnedblocks = returnedblocks.concat(<Block key = {selectedblock.id} imgid = {selectedblock.imgid} id={selectedblock.id} color={selectedblock.color} comments={selectedblock.comments}></Block>)
      }
      this.setState({blocks: returnedblocks})
    });
    }



    addBlock = (color) => {
        const id = this.state.blocks.length
        const imgid = Number(this.state.blocks.length + 110)   
      var colObject = {
        id: id,
        col : color,
        com : [],
        imgid : imgid
     }
      axios.post('http://localhost:3001/newpost',colObject)
      .then((response) => {
        console.log(response)
        this.Updatefeed()
      }
      )
      .catch(error => {
        console.error('There was an error!', error);
      });
    }

    pickColorlayout = () => {
      if (this.state.showopt == false) {
        this.setState({
          showopt: true
        })
      }
      else{
        this.setState({
            showopt: false
        })
      }
    }
    
    render() {

      return (  
        <div className = 'mainfeed'>
        <div className="input">
        {this.state.blocks}
        </div>
        <div>
          <button className="button" onClick={this.pickColorlayout}>+ Post Block</button>
        </div>
        { this.state.showopt ?         <div className="pickblock">
        <div>
            <button className="red"  onClick={() => this.addBlock("rgb(134, 5, 5)")}></button>
            <button className="blue" onClick={() => this.addBlock("rgb(21, 0, 141)")}></button>
        </div>
        <div>
            <button className="green"  onClick={() => this.addBlock("rgb(59, 110, 0)")}></button>
            <button className="purple"  onClick={() => this.addBlock("rgb(58, 0, 97)")}></button>
        </div>
     </div>: null }   
      </div>
      );
    }
  }


  export default Feed;