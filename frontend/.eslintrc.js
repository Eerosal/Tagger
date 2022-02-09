module.exports = {
    "env": {
        "browser": true,
        "es2021": true
    },
    extends: [
        "plugin:react/recommended",
        "airbnb",
        "react-app",
        "react-app/jest",
        "prettier",
    ],
    "parser": "@typescript-eslint/parser",
    "parserOptions": {
        "ecmaFeatures": {
            "jsx": true
        },
        "ecmaVersion": "latest",
        "sourceType": "module"
    },
    "plugins": [
        "prettier",
        "react",
        "@typescript-eslint"
    ],
    "rules": {
        quotes: [2, "double", { avoidEscape: true }],
        indent: ["error", 4, { SwitchCase: 1}],
        "max-len": ["error", { code: 80 }],
        "no-param-reassign": [2, { props: false }],
        "import/extensions": "off",
        "no-alert": "off",
        "react/react-in-jsx-scope": "off",
        "react/jsx-filename-extension": [1, {
            extensions: [".js", ".jsx", ".ts", ".tsx"] }],
        "jsx-a11y/label-has-associated-control": ["error", {
            "required": {
                "some": ["nesting", "id"]
            }
        }],
        "jsx-a11y/label-has-for": ["error", {
            "required": {
                "some": ["nesting", "id"]
            }
        }],
    },
    "settings": {
        "import/resolver": {
            "node": {
                "extensions": [".js", ".jsx", ".ts", ".tsx"]
            }
        }
    }
}
