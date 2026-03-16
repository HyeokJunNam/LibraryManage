const TOKEN_KEY = "accessToken";

function setToken(token){
    localStorage.setItem(TOKEN_KEY, token);
}

function getToken(){
    return localStorage.getItem(TOKEN_KEY);
}

function removeToken(){
    localStorage.removeItem(TOKEN_KEY);
}