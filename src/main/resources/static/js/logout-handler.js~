// document.addEventListener("DOMContentLoaded", function () {
//     const btn = document.getElementById('mobile-menu-button');
//     const menu = document.getElementById('mobile-menu');
//     if (btn && menu) {
//         btn.addEventListener('click', () => {
//             menu.classList.toggle('hidden');
//         });
//     }
//
//     const logoutBtn = document.getElementById("logout-link");
//     if (logoutBtn) {
//         logoutBtn.addEventListener("click", function (event) {
//             event.preventDefault();
//
//             Swal.fire({
//                 title: 'Bạn có chắc muốn đăng xuất?',
//                 text: "Phiên làm việc của bạn sẽ kết thúc.",
//                 icon: 'warning',
//                 showCancelButton: true,
//                 confirmButtonColor: '#3085d6',
//                 cancelButtonColor: '#d33',
//                 confirmButtonText: 'Đăng xuất',
//                 cancelButtonText: 'Hủy'
//             }).then((result) => {
//                 if (result.isConfirmed) {
//                     window.location.href = '/logout';
//                 }
//             });
//         });
//     }
// });

function confirmLogout() {
    Swal.fire({
        title: 'Bạn có chắc muốn đăng xuất?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Đăng xuất',
        cancelButtonText: 'Hủy'
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = '/logout';
        }
    });
}