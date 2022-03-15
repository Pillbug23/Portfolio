console.log("script connected.");

var heart_status = 0; // 0 is empty and 1 is filled
var date = new Date();
var year = date.getFullYear();
var month = date.getMonth();
var day = date.getDate();

const previousDate = (year, day, month) => {
  if (day - 1 < 1) {
    month -= 1;
  } else {
    day -= 1;
  }
  return [year, day, month];
};

const dateToString = (year, day, month) =>
  String(year) + "-" + String(day) + String(month);

document.getElementById("heart-button").addEventListener("click", () => {
  let heart = document.getElementById("heart-button");
  if (heart_status == 0) {
    heart.src = "static/heart-filled.png";
    heart_status = 1;
    data = { image_url: document.getElementById("apod-image").src,
             date: document.getElementById("apod-date").innerHTML};
    // TODO: update the database and mark this image as a favorite image.
    fetch('http://localhost:8080/api/add', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    })
    .then(response => response.json())
    .then(json => {
        console.log(json);
    });
  } else {
    heart_status = 0;
    heart.src = "static/heart.png";
    data = { date: document.getElementById("apod-date").innerHTML}
    // TODO: update the database and un-mark this image as a favorite image.
    fetch('http://localhost:8080/api/delete', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    })
    .then(response => response.json())
    .then(json => {
        console.log(json);
    });
  }
});

document.getElementById("next-button").addEventListener("click", () => {
  document.getElementById("heart-button").src = "static/heart.png";
  heart_status = 0;
  [year, day, month] = previousDate(year, day, month);
  fetch(
    "https://api.nasa.gov/planetary/apod?api_key=n8DxZ7KZETyN20MsfyMRjKpZYRmHqzSB5QPPIX4d&date=" +
      dateToString(year, day, month)
  )
    .then((r) => r.json())
    .then((r) => {
      console.log("current APOD data:");
      console.log(r);
      document.getElementById("apod-date").innerHTML = r.date;
      document.getElementById("apod-image").src = r.url;
      document.getElementById("apod-title").innerHTML = r.title;
      document.getElementById("apod-p").innerHTML = r.explanation;
    });
});
