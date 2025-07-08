import { createPinia } from 'pinia'

const store = createPinia()

export const setupStore = (app)=> {
  app.use(store)
}

export { store }
