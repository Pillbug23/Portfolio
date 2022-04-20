import React,{useState, useEffect} from 'react';
import style from './Saved.css';
import {Typography} from '@material-ui/core';
import {Card,CardMedia,CardContent} from '@material-ui/core';
import Axios from 'axios';
import useStyles from '../Trail/List/styles';

function Saved() {
    const [FavoriteStatus, setFavoriteStatus] = useState([])
    const [display, setDisplay] = useState(false)

    const classes = useStyles();

    const favorites = () => {
    Axios.get('http://localhost:5000/favorite', {
      })
      .then((response) => {
          setFavoriteStatus(response.data)
      })
    }
    

    useEffect(()=> {
      if (display) {
        favorites()
      }
    },[])

    return (
        <div>
           {display ? (
              <div className="Favs">
          {FavoriteStatus?.map((place, i) => (   
              <Card elevation={1}
              style={{backgroundImage: 'linear-gradient(90deg, #74EBD5 0%, #9FACE6 100%)',border: '6px solid #fd5e53', margin: '15px'}}
              >
              <CardMedia
                style={{ height: 30}}
                title={place.name}
              />
              <CardContent style={{ textAlign:'left'}}>
                  <Typography gutterBottom variant="h4" > ~{place.name}~</Typography>
                  <Typography component="legend" style={{padding:'10px'}}>Description: {place.description}</Typography>
                  <Typography component="legend" style={{padding:'10px'}}>Directions: {place.directions}</Typography>
                  <Typography component="legend" style={{padding:'10px'}}>City: {place.city}</Typography>
                  <Typography component="legend" style={{padding:'10px'}}>Favorites: {place.shalong} </Typography>
              </CardContent>
              </Card>
          ))}          
          </div> 
            ) : (
            <h1>Viewing of Favorited Trails is only accessible through backend</h1>
            )}
      </div>
      );
    }
    
export default Saved;