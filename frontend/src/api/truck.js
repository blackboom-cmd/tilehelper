import axios from "axios";

const BASE = "/api/truck";

export function getTruckList() {
  return axios.get(`${BASE}/list`, {
    params: { _t: Date.now() }
  });
}

export function addTruck(data) {
  return axios.post(`${BASE}/add`, data);
}

export function updateTruck(data) {
  return axios.put(`${BASE}/update`, data);
}

export function deleteTrucks(ids) {
  return axios.delete(`${BASE}/delete`, { data: ids });
}
