import Vue from 'vue'
import Router from 'vue-router'
import DemoAsm from '@/components/DemoAsm'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'DemoAsm',
      component: DemoAsm
    }
  ]
})
