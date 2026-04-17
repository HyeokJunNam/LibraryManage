class ApiError extends Error {
    constructor(message, status, data = null) {
        super(message);
        this.name = "ApiError";
        this.status = status;
        this.data = data;
    }
}

async function apiRequest(url, options = {}) {
    const response = await fetch(url, buildRequestOptions(options));
    const data = await parseResponseBody(response);

    if (!response.ok) {
        const message = extractApiErrorMessage(data) || "요청 처리 중 오류가 발생했습니다.";
        throw new ApiError(message, response.status, data);
    }

    return data;
}

function buildRequestOptions(options) {
    const method = options.method || "GET";
    const headers = new Headers(options.headers || {});
    const requestOptions = {
        method,
        headers,
        credentials: options.credentials || "same-origin"
    };

    if (options.body !== undefined && options.body !== null) {
        if (!headers.has("Content-Type")) {
            headers.set("Content-Type", "application/json");
        }

        requestOptions.body =
            typeof options.body === "string" ? options.body : JSON.stringify(options.body);
    }

    return requestOptions;
}

async function parseResponseBody(response) {
    const contentType = response.headers.get("Content-Type") || "";

    if (isJsonContentType(contentType)) {
        try {
            return await response.json();
        } catch (error) {
            console.error(error);
            return null;
        }
    }

    try {
        const text = await response.text();
        return text && text.trim() !== "" ? text : null;
    } catch (error) {
        console.error(error);
        return null;
    }
}

function isJsonContentType(contentType) {
    const normalized = contentType.toLowerCase();

    return normalized.includes("application/json")
        || normalized.includes("application/problem+json")
        || normalized.endsWith("+json");
}

function extractApiErrorMessage(data) {
    if (!data) {
        return null;
    }

    if (typeof data === "string" && data.trim() !== "") {
        return data.trim();
    }

    if (typeof data !== "object") {
        return null;
    }

    return pickFirstNonEmptyString(
        data.detail,
        data.message,
        data.result,
        data.title,
        data.code
    );
}

function pickFirstNonEmptyString(...values) {
    for (const value of values) {
        if (typeof value === "string" && value.trim() !== "") {
            return value.trim();
        }
    }

    return null;
}

async function apiGet(url, options = {}) {
    return apiRequest(url, {
        ...options,
        method: "GET"
    });
}

async function apiPost(url, body, options = {}) {
    return apiRequest(url, {
        ...options,
        method: "POST",
        body
    });
}

async function apiPut(url, body, options = {}) {
    return apiRequest(url, {
        ...options,
        method: "PUT",
        body
    });
}

async function apiPatch(url, body, options = {}) {
    return apiRequest(url, {
        ...options,
        method: "PATCH",
        body
    });
}

async function apiDelete(url, options = {}) {
    return apiRequest(url, {
        ...options,
        method: "DELETE"
    });
}