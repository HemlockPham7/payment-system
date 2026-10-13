import axios from 'axios'
import { BASE_URL } from '@root/api/utils/basePath.ts'

const paymentAxios = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
})

export default paymentAxios
