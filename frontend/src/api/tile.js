import axios from "axios";

export function getTileList() {
    return axios.get("http://localhost:8080/tile/list", {
        params: { _t: Date.now() }
    });
}