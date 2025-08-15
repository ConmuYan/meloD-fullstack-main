module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es2021: true
  },
  'extends': [
    'plugin:vue/vue3-essential',
    'eslint:recommended',
    '@vue/typescript/recommended'
  ],
  parserOptions: {
    ecmaVersion: 2020,
    parser: '@typescript-eslint/parser',
    sourceType: 'module'
  },
  plugins: [
    'vue',
    '@typescript-eslint'
  ],
  rules: {
    // 基础规则
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-undef': 0,
    
    // Vue相关规则
    'vue/multi-word-component-names': 0,
    'vue/no-unused-vars': 'off',
    'vue/no-mutating-props': 'off',
    'vue/no-setup-props-destructure': 'off',
    'vue/no-deprecated-slot-attribute': 'off',
    
    // TypeScript相关规则
    "@typescript-eslint/no-unused-vars": ["off"],
    "@typescript-eslint/no-explicit-any": ["off"],
    "@typescript-eslint/explicit-module-boundary-types": ["off"],
    "@typescript-eslint/no-non-null-assertion": ["off"],
    "@typescript-eslint/ban-ts-comment": ["off"],
    
    // 其他常见警告的处理
    'prefer-const': 'off',
    'no-var': 'off',
    'no-unused-vars': 'off'
  },
  globals: {
    // 定义全局变量，避免未定义警告
    'ResponseBody': 'readonly',
    'defineProps': 'readonly',
    'defineEmits': 'readonly',
    'defineExpose': 'readonly',
    'withDefaults': 'readonly'
  }
}
