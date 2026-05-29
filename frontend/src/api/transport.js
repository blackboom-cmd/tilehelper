import axios from "axios";

const BASE = "/api/transport";

export function getTransportList() {
  return axios.get(`${BASE}/list`, {
    params: { _t: Date.now() }
  });
}

export function addTransport(data) {
  return axios.post(`${BASE}/add`, data);
}

export function updateTransport(data) {
  return axios.put(`${BASE}/update`, data);
}

export function updateTransportStatus(data) {
  return axios.post(`${BASE}/updateStatus`, data);
}

export function deleteTransports(ids) {
  return axios.delete(`${BASE}/delete`, { data: ids });
}
