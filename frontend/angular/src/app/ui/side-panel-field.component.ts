import { Component, Input, ViewEncapsulation } from '@angular/core'

@Component({
  selector: 'app-side-panel-field',
  standalone: true,
  encapsulation: ViewEncapsulation.None,
  host: { class: 'side-panel-field-host' },
  template: `
    <label class="side-panel-field">
      <span class="side-panel-field__label">{{ label }}</span>
      <div class="side-panel-field__control">
        <ng-content />
      </div>
    </label>
  `,
})
export class SidePanelFieldComponent {
  @Input({ required: true }) label!: string
}
