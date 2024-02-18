function loadGetMsg() {
    let nameVar = document.getElementById("title").value;
    const xhttp = new XMLHttpRequest();
    xhttp.onload = function() {
        const movieData = this.responseText;
        buildTableFromJson(movieData);
    };
    xhttp.open("GET", "/movies?name="+ encodeURIComponent(nameVar));
    xhttp.send();
}

function buildTableFromJson(movieData) {
    const movieObject = JSON.parse(movieData);
    const tableHtml = "<table border='1'>" +
        buildRowsFromObject(movieObject) +
        "</table>";
    document.getElementById("getrespmsg").innerHTML = tableHtml;
}

function buildRowsFromObject(obj) {
    return Object.entries(obj).map(([key, value]) => {
        return `<tr><td>${key}</td><td>${formatValue(value)}</td></tr>`;
    }).join("");
}

function formatValue(value) {
    if (Array.isArray(value)) {
        return "" + value.map(formatValue).join(", ") + "";
    } else if (typeof value === 'object') {
        return "{" + Object.entries(value).map(([subKey, subValue]) => `${subKey}: \t\t\t\t${formatValue(subValue)}`).join(", ") + "}";
    } else {
        return value;
    }
}
