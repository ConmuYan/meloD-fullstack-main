// 登录规则
const validateName = (rule, value, callback) => {
  if (!value) {
    return callback(new Error("用户名不能为空"));
  } else {
    callback();
  }
};

export const validatePassword = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("密码不能为空"));
  } else {
    callback();
  }
};

// 注册用户名验证
const validateSignUpName = (rule, value, callback) => {
  if (!value) {
    return callback(new Error("用户名不能为空"));
  } else {
    callback();
  }
};

// 注册密码验证
const validateSignUpPassword = (rule, value, callback) => {
  if (!value) {
    return callback(new Error("密码不能为空"));
  } else if (value.length < 8) {
    return callback(new Error("密码长度不能少于8位"));
  } else if (!/[a-z]/.test(value)) {
    return callback(new Error("密码必须包含至少一个小写字母"));
  } else if (!/[A-Z]/.test(value)) {
    return callback(new Error("密码必须包含至少一个大写字母"));
  } else {
    callback();
  }
};

// 个人资料修改密码验证
export const validateUpdatePassword = (rule, value, callback) => {
  if (!value) {
    return callback(new Error("新密码不能为空"));
  } else if (value.length < 8) {
    return callback(new Error("密码长度不能少于8位"));
  } else if (!/[a-z]/.test(value)) {
    return callback(new Error("密码必须包含至少一个小写字母"));
  } else if (!/[A-Z]/.test(value)) {
    return callback(new Error("密码必须包含至少一个大写字母"));
  } else {
    callback();
  }
};

export const SignInRules = {
  username: [{ validator: validateName, trigger: "blur", min: 3 }],
  password: [{ validator: validatePassword, trigger: "blur", min: 3 }],
};

// 邮箱登录规则
export const EmailSignInRules = {
  email: [
    { required: true, message: "邮箱不能为空", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] },
  ],
  password: [{ validator: validatePassword, trigger: "blur", min: 3 }],
};

// 注册规则
export const SignUpRules = {
  username: [{ required: true, validator: validateSignUpName, trigger: "blur" }],
  password: [{ required: true, validator: validateSignUpPassword, trigger: "blur" }],
  sex: [{ required: true, message: "请选择性别", trigger: "change" }],
  phoneNum: [{ message: "请输入手机号码", trigger: "blur" }],
  email: [
    { required: true, message: "邮箱不能为空", trigger: "blur" },
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: ["blur", "change"],
    },
  ],
  birth: [{ required: true, type: "date", message: "请选择日期", trigger: "change" }],
  introduction: [{ message: "请输入介绍", trigger: "blur" }],
  location: [{ message: "请输入地区", trigger: "change" }],
};

// 个人资料更新规则
export const PersonalDataRules = {
  username: [{ required: true, validator: validateSignUpName, trigger: "blur" }],
  email: [
    { required: true, message: "邮箱不能为空", trigger: "blur" },
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: ["blur", "change"],
    },
  ],
};
