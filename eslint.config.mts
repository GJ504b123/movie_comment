import pluginVue from 'eslint-plugin-vue'
import tsParser from '@typescript-eslint/parser'
import prettier from 'eslint-plugin-prettier'
// import vuePrettier from '@vue/eslint-config-prettier'

export default [
  {
    files: ['**/*.vue', '**/*.ts'],
    languageOptions: {
      parser: tsParser,
      parserOptions: {
        ecmaVersion: 'latest',
        sourceType: 'module'
      }
    },
    plugins: {
      vue: pluginVue,
      prettier
    },
    rules: {
      'prettier/prettier': 'off',
      'vue/multi-word-component-names': 'off'
    }
  },
  ...pluginVue.configs['flat/recommended'],
  // vuePrettier
]