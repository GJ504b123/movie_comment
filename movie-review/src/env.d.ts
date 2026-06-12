/// <reference types="vite/client" />

// mock 库为纯 JS（无类型声明），按 any 模块处理
declare module '*/mock/index.js'

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const component: DefineComponent<{}, {}, any>
  export default component
}
