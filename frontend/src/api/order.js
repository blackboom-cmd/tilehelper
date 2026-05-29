import axios from "axios";

const BASE = "/api/order";

export function getOrderList() {
  return axios.get(`${BASE}/list`, {
    params: { _t: Date.now() }
  });
}

export function addOrder(data) {
  return axios.post(`${BASE}/add`, data);
}

export function updateOrder(data) {
  return axios.put(`${BASE}/update`, data);
}

export function updateOrderStatus(data) {
  return axios.post(`${BASE}/updateStatus`, data);
}

export function deleteOrders(ids) {
  return axios.delete(`${BASE}/delete`, { data: ids });
}
