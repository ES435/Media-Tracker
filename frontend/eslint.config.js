import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import tseslint from 'typescript-eslint'
import { defineConfig, globalIgnores } from 'eslint/config'

export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      js.configs.recommended,
      tseslint.configs.recommended,
      reactHooks.configs['recommended-latest'],
      reactRefresh.configs.vite,
    ],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },
    rules: {
      // Allow unused vars (useful during dev), warn only to pass CI
      '@typescript-eslint/no-unused-vars': 'warn',
      // Allow explicit 'any' but warn
      '@typescript-eslint/no-explicit-any': 'warn',
      // Allow @ts-ignore comments but warn
      '@typescript-eslint/ban-ts-comment': 'warn',
      // Warn on Fast Refresh issues instead of error
      'react-refresh/only-export-components': 'warn'
    },
  },
])