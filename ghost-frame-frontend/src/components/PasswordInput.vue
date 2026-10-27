<script setup>
import { ref } from 'vue'

defineProps({
  id: { type: String, required: true },
  modelValue: { type: String, default: '' },
  autocomplete: { type: String, default: 'current-password' },
  placeholder: { type: String, default: '' },
  invalid: { type: Boolean, default: false },
  describedBy: { type: String, default: undefined },
})

const emit = defineEmits(['update:modelValue'])

const inputRef = ref(null)
const visible = ref(false)

function focus() {
  inputRef.value?.focus()
}

function toggleVisible() {
  visible.value = !visible.value
}

defineExpose({ focus })
</script>

<template>
  <div class="password-field">
    <input :id="id" ref="inputRef" :value="modelValue" :type="visible ? 'text' : 'password'"
      :autocomplete="autocomplete" :placeholder="placeholder"
      :class="{ 'input-invalid': invalid }" :aria-invalid="invalid" :aria-describedby="describedBy"
      @input="emit('update:modelValue', $event.target.value)" />
    <button type="button" class="password-toggle" :aria-pressed="visible"
      :aria-label="visible ? '隐藏密码' : '显示密码'" @click="toggleVisible">
      <svg v-if="visible" viewBox="0 0 24 24" aria-hidden="true">
        <path d="M3 3l18 18" />
        <path d="M10.5 10.7a2 2 0 0 0 2.8 2.8" />
        <path d="M9.9 5.2A10.8 10.8 0 0 1 12 5c5.5 0 9.5 4.2 10.5 7a12 12 0 0 1-2.1 3.4" />
        <path d="M6.1 6.2C4.2 7.5 2.7 9.4 1.5 12c1 2.8 5 7 10.5 7 1.2 0 2.3-.2 3.3-.6" />
      </svg>
      <svg v-else viewBox="0 0 24 24" aria-hidden="true">
        <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z" />
        <circle cx="12" cy="12" r="3" />
      </svg>
    </button>
  </div>
</template>

<style scoped>
.password-field {
  position: relative;
}

.password-field input {
  padding-right: 40px;
}

.password-field input::-ms-reveal,
.password-field input::-ms-clear {
  display: none;
}

.password-toggle {
  position: absolute;
  top: 50%;
  right: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  transform: translateY(-50%);
  border: none;
  border-radius: 4px;
  background: transparent;
  color: #6b7280;
  cursor: pointer;
}

.password-toggle svg {
  width: 18px;
  height: 18px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.password-toggle:hover {
  color: var(--accent);
  background: #eff4ff;
}
</style>
