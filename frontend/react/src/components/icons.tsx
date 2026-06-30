import type { SVGProps } from 'react'

type IconProps = SVGProps<SVGSVGElement> & { size?: number }

function Icon({ size = 24, children, ...rest }: IconProps & { children: React.ReactNode }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" aria-hidden {...rest}>
      {children}
    </svg>
  )
}

export function WalletIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <path
        d="M5.125 20.775H4.6a2.35 2.35 0 0 1-1.675-.7 2.35 2.35 0 0 1-.7-1.675V5.1c0-.625.233-1.158.7-1.6.467-.442 1-.675 1.6-.675H18.9c.6 0 1.133.233 1.6.7.467.467.7 1 .7 1.6v.2h-7.725c-1.117 0-2.03.354-2.738 1.062-.708.709-1.062 1.621-1.062 2.738v5.8c0 1.117.354 2.03 1.062 2.738.708.709 1.621 1.063 2.738 1.063h7.725v.2c0 .625-.233 1.158-.7 1.6-.467.442-1 .675-1.6.675H5.125Zm7.925-4.05c-.542 0-.998-.185-1.369-.556a1.87 1.87 0 0 1-.556-1.369V9.1c0-.542.185-.998.556-1.369.371-.37.827-.556 1.369-.556h6.775c.542 0 .998.185 1.369.556.371.371.556.827.556 1.369v5.8c0 .542-.185.998-.556 1.369-.371.371-.827.556-1.369.556H13.05Zm2.9-3.225a1.45 1.45 0 0 0 1.05-.425 1.45 1.45 0 0 0 .425-1.05 1.45 1.45 0 0 0-.425-1.05 1.45 1.45 0 0 0-1.05-.425 1.45 1.45 0 0 0-1.05.425 1.45 1.45 0 0 0-.425 1.05c0 .417.142.767.425 1.05.283.283.633.425 1.05.425Z"
        fill="currentColor"
      />
    </Icon>
  )
}

export function StarIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <path
        d="M5.825 22l1.625-7.025L2 10.25l7.2-.625L12 3l2.8 6.625 7.2.625-5.45 4.725L18.175 22 12 18.275 5.825 22Zm2.025-2.8L12 16.85l4.15 2.35-1.1-4.725 3.65-3.175-4.8-.425L12 6.6l-1.9 4.4-4.8.425 3.65 3.175-1.1 4.7Z"
        fill="currentColor"
      />
    </Icon>
  )
}

export function HouseIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <path d="M15 21V13a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v8" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M3 10c0-.291.063-.578.186-.842.123-.264.301-.506.523-.728L10.709 2.473A2 2 0 0 1 12 2a2 2 0 0 1 1.291.473l7 7.057c.222.222.4.464.523.728.123.264.186.551.186.842V19a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V10Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </Icon>
  )
}

export function NoteStackIcon(props: IconProps) {
  return (
    <svg width={props.size ?? 24} height={props.size ?? 24} viewBox="0 0 25 24" fill="none" aria-hidden {...props}>
      <path
        d="M7.827 20.253V9.228c0-.55.2-1.017.6-1.4.4-.384.875-.576 1.425-.576h11c.55 0 1.02.196 1.412.588.392.392.588.863.588 1.413v8l-5 5H9.827c-.55 0-1.02-.196-1.412-.588a1.99 1.99 0 0 1-.588-1.412ZM2.852 6.503c-.1-.55.008-1.046.325-1.487.317-.442.75-.713 1.3-.813l10.85-1.925c.55-.1 1.046.008 1.488.325.442.317.713.75.813 1.3l.25 1.35H9.827c-1.1 0-2.042.392-2.825 1.175-.783.784-1.175 1.726-1.175 2.826v9.55c-.267-.15-.496-.35-.688-.6a1.7 1.7 0 0 1-.362-.85L2.852 6.503ZM20.827 16.253h-4v4l4-4Z"
        fill="currentColor"
      />
    </svg>
  )
}

export function WalletOutlinedIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <path
        d="M5.125 18.6996V18.8996V5.09961V18.6996ZM5.125 20.7746C4.60933 20.7746 4.16792 20.591 3.80075 20.2239C3.43358 19.8567 3.25 19.4153 3.25 18.8996V5.09961C3.25 4.58394 3.43358 4.14253 3.80075 3.77536C4.16792 3.40819 4.60933 3.22461 5.125 3.22461H18.9C19.4157 3.22461 19.8571 3.40819 20.2242 3.77536C20.5914 4.14253 20.775 4.58394 20.775 5.09961V7.67461H18.9V5.09961H5.125V18.8996H18.9V16.3246H20.775V18.8996C20.775 19.4153 20.5914 19.8567 20.2242 20.2239C19.8571 20.591 19.4157 20.7746 18.9 20.7746H5.125ZM13.0488 16.8246C12.5163 16.8246 12.0625 16.6361 11.6875 16.2591C11.3125 15.8821 11.125 15.4289 11.125 14.8996V9.09736C11.125 8.56553 11.3135 8.11211 11.6905 7.73711C12.0675 7.36211 12.5207 7.17461 13.05 7.17461H19.8263C20.3587 7.17461 20.8125 7.36311 21.1875 7.74011C21.5625 8.11711 21.75 8.57028 21.75 9.09961V14.9019C21.75 15.4337 21.5615 15.8871 21.1845 16.2621C20.8075 16.6371 20.3543 16.8246 19.825 16.8246H13.0488ZM19.875 14.9496V9.04961H13V14.9496H19.875ZM15.9485 13.4746C16.3578 13.4746 16.7063 13.3314 16.9938 13.0449C17.2812 12.7584 17.425 12.4104 17.425 12.0011C17.425 11.5918 17.2817 11.2434 16.9952 10.9559C16.7087 10.6684 16.3608 10.5246 15.9515 10.5246C15.5422 10.5246 15.1938 10.6679 14.9062 10.9544C14.6188 11.2409 14.475 11.5888 14.475 11.9981C14.475 12.4074 14.6182 12.7559 14.9047 13.0434C15.1912 13.3309 15.5392 13.4746 15.9485 13.4746Z"
        fill="currentColor"
      />
    </Icon>
  )
}

export function FoldersIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <path
        d="M20 17C20.5304 17 21.0391 16.7893 21.4142 16.4142C21.7893 16.0391 22 15.5304 22 15V9C22 8.46957 21.7893 7.96086 21.4142 7.58579C21.0391 7.21071 20.5304 7 20 7H16.1C15.7655 7.00328 15.4355 6.92261 15.1403 6.76538C14.8451 6.60815 14.594 6.37938 14.41 6.1L13.6 4.9C13.4179 4.62347 13.17 4.39648 12.8785 4.2394C12.587 4.08231 12.2611 4.00005 11.93 4H8C7.46957 4 6.96086 4.21071 6.58579 4.58579C6.21071 4.96086 6 5.46957 6 6V15C6 15.5304 6.21071 16.0391 6.58579 16.4142C6.96086 16.7893 7.46957 17 8 17H20Z"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M2 8V19C2 19.5304 2.21071 20.0391 2.58579 20.4142C2.96086 20.7893 3.46957 21 4 21H18"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </Icon>
  )
}

export function ChartIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <path d="M12 16V21M16 14V21M20 10V21M22 3 13.354 11.646a1 1 0 0 1-1.415 0L9.354 8.354a1 1 0 0 0-1.415 0L2 15M4 18V21M8 14V21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </Icon>
  )
}

export function TrendUpIcon({ size = 16, ...rest }: IconProps) {
  return (
    <svg width={size} height={size} viewBox="0 0 16 16" fill="none" aria-hidden {...rest}>
      <path d="M14.667 4.667 9 10.333 5.667 7 1.334 11.333" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M10.666 4.667h4v4" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  )
}

export function TrendDownIcon({ size = 16, ...rest }: IconProps) {
  return (
    <svg width={size} height={size} viewBox="0 0 16 16" fill="none" aria-hidden {...rest}>
      <path d="M14.667 11.333 9 5.667 5.667 9 1.334 4.667" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M10.666 11.333h4v-4" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  )
}

export function LockIcon(props: IconProps) {
  return (
    <Icon size={props.size ?? 12} {...props}>
      <path
        d="M3.087 10.967a1.3 1.3 0 0 1-.925-.375 1.3 1.3 0 0 1-.375-.925V5.039c0-.296.105-.549.315-.76.21-.21.463-.315.76-.315h.457V3.078c0-.688.238-1.272.715-1.75.477-.479 1.058-.719 1.743-.719.685 0 1.266.24 1.743.719.477.478.715 1.062.715 1.75v.886h.457c.296 0 .549.105.76.315.21.21.315.463.315.76v4.853c0 .296-.105.549-.315.76a1.3 1.3 0 0 1-.76.315H3.087Zm2.914-2.539a1.04 1.04 0 0 0 .76-.32 1.04 1.04 0 0 0 .32-.76 1.04 1.04 0 0 0-.32-.76 1.04 1.04 0 0 0-.76-.32 1.04 1.04 0 0 0-.76.32 1.04 1.04 0 0 0-.32.76c0 .265.105.491.294.68.189.189.415.284.68.284ZM4.618 3.964h2.765V3.074c0-.384-.134-.711-.401-.983a1.35 1.35 0 0 0-.98-.406c-.385 0-.712.135-.98.406-.269.27-.404.599-.404.983v.89Z"
        fill="currentColor"
      />
    </Icon>
  )
}

export function LogoutIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </Icon>
  )
}

export function EyeIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7Z" stroke="currentColor" strokeWidth="1.5" />
      <circle cx="12" cy="12" r="3" stroke="currentColor" strokeWidth="1.5" />
    </Icon>
  )
}

export function EyeOffIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <path d="m2 2 20 20M6.7 6.7A10.7 10.7 0 0 0 2 12s3.5 7 10 7c2 0 3.8-.7 5.2-1.8M10.6 10.6a3 3 0 0 0 4.2 4.2M17.3 17.3A10.7 10.7 0 0 0 22 12s-3.5-7-10-7c-1.1 0-2.1.2-3 .5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
    </Icon>
  )
}

export function CircleXIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <path d="M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20Z" stroke="currentColor" strokeWidth="2" />
      <path d="m15 9-6 6M9 9l6 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
    </Icon>
  )
}

export function CrossIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <path d="M18 6L6 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M6 6L18 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </Icon>
  )
}

export function CheckIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <path d="M20 6L9 17L4 12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </Icon>
  )
}

export function SearchIcon(props: IconProps) {
  return (
    <Icon {...props}>
      <circle cx="11" cy="11" r="7" stroke="currentColor" strokeWidth="2" />
      <path d="M20 20L16.5 16.5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
    </Icon>
  )
}

export function ChevronDownIcon({ size = 12, className, ...rest }: IconProps) {
  return (
    <svg
      width={size}
      height={Math.round(size / 2)}
      viewBox="0 0 12 6"
      fill="none"
      aria-hidden
      className={className}
      {...rest}
    >
      <path
        d="M1.5 1.5L6 5L10.5 1.5"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}

export function ChevronSidebarIcon({ className, ...rest }: Omit<IconProps, 'size'>) {
  return (
    <svg
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      aria-hidden
      className={className}
      {...rest}
    >
      <path
        d="M6 9l6 6 6-6"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}

export function ChevronLeftIcon({ size = 16, className, ...rest }: IconProps) {
  return (
    <svg width={size} height={size} viewBox="0 0 16 16" fill="none" aria-hidden className={className} {...rest}>
      <path
        d="M10 3L5 8L10 13"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}

export function ChevronRightIcon({ size = 16, className, ...rest }: IconProps) {
  return (
    <svg width={size} height={size} viewBox="0 0 16 16" fill="none" aria-hidden className={className} {...rest}>
      <path
        d="M6 3L11 8L6 13"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}
