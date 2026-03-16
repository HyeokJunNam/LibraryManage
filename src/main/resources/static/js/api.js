async function apiFetch(url, options = {}) {

    const token = getToken();

    const headers = {
        ...(options.headers || {})
    };

    if(token){
        headers["Authorization"] = token;
    }

    const response = await fetch(url,{
        ...options,
        headers
    });

    if(response.status === 401){
        removeToken();
        location.href="/login";
        return;
    }

    return response;
}