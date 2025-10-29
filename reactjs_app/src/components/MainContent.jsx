import React, { useState } from 'react';
import { useRef, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import './MainContent.css';
import { createGoogleForm } from '../utils/quizApi';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import {
  faFileAlt,
  faUpload,
  faPaperclip,
  faPenToSquare,
  faCopy,
  faSearch,
  faPaperPlane,
  faXmark,
  faImage,
  faCalendarAlt,
  faSpinner, // 👈 thêm dòng này vào cuối danh sách
  faTrash,
} from '@fortawesome/free-solid-svg-icons';


const MainContent = () => {

  const [selectedLang, setSelectedLang] = useState('Japanese');

  const chatContainerRef = useRef(null); // Thêm ref cho container chat

  const [showQuizLink, setShowQuizLink] = useState(false);
  const [quizUrl, setQuizUrl] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  // CHATBOT STATES
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [selectedImage, setSelectedImage] = useState(null);
  const [isSending, setIsSending] = useState(false);

  // FILE STATES (Quiz Upload)
  const [selectedFiles, setSelectedFiles] = useState([]);

  // HISTORY SIDEBAR
  const [historyOpen, setHistoryOpen] = useState(false);
  const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  // FILE HANDLING

  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
    }
  }, [messages]); // chạy mỗi khi messages thay đổi


  //==============DELETE ALL 
  // 🗑️ Hàm xóa toàn bộ file trên server
  const handleDeleteAll = async () => {
    if (!window.confirm("⚠️ Are you sure you want to delete all uploaded files?")) return;

    try {
      const res = await fetch("http://10.1.5.24:8080/api/v1/documents/delete-all", {
        method: "DELETE",
        headers: {
          "Accept": "application/json",
        },
      });

      if (!res.ok) {
        toast.error("❌ Failed to delete all files!", {
          position: "top-right",
          autoClose: 2500,
        });
        return;
      }

      toast.success("✅ All files have been deleted successfully!", {
        position: "top-right",
        autoClose: 2000,
      });

      // Load lại danh sách sau khi xóa
      fetchServerFiles();
    } catch (err) {
      console.error("❌ Error deleting all files:", err);
      toast.error("🚨 Error occurred while deleting files!", {
        position: "top-right",
        autoClose: 2500,
      });
    }
  };

  // 🗑️ Hàm xóa từng file
  const handleDeleteFile = async (fileId, fileName) => {
    if (!window.confirm(`⚠️ Are you sure you want to delete "${fileName}"?`)) return;

    try {
      const res = await fetch(`http://10.1.5.24:8080/api/v1/documents/${fileId}`, {
        method: "DELETE",
        headers: {
          "Accept": "application/json",
        },
      });

      if (!res.ok) {
        toast.error(`❌ Failed to delete "${fileName}"`, {
          position: "top-right",
          autoClose: 2500,
        });
        return;
      }

      toast.success(`✅ "${fileName}" deleted successfully!`, {
        position: "top-right",
        autoClose: 2000,
      });

      // Cập nhật lại danh sách file
      fetchServerFiles();
    } catch (err) {
      console.error("❌ Error deleting file:", err);
      toast.error("🚨 Error occurred while deleting the file!", {
        position: "top-right",
        autoClose: 2500,
      });
    }
  };


  // SEND MESSAGE
  const handleSend = async () => {
    if (input.trim() === '' && !selectedImage) return;

    const imageToSend = selectedImage ? URL.createObjectURL(selectedImage) : null;
    setSelectedImage(null);
    const imgInput = document.getElementById('chatImageInput');
    if (imgInput) imgInput.value = '';

    setIsSending(true);

    const userMessage = {
      sender: 'user',
      text: input,
      image: imageToSend,
    };
    setMessages((prev) => [...prev, userMessage]);
    setInput('');

    try {
      let response;
      let raw;
      let botReply = '';

      if (imageToSend) {
        const formData = new FormData();
        formData.append('message', userMessage.text);
        formData.append('file', selectedImage);

        response = await fetch('http://localhost:8080/chat_image', {
          method: 'POST',
          body: formData,
        });

        raw = await response.text();
      } else {
        // ✅ Cấu trúc JSON body đúng như hình Postman
        response = await fetch('http://10.1.5.24:8080/chat/ask', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            question: userMessage.text,
            limit: 10,
            score_threshold: 0.3,
            language: selectedLang
          }),
        });
        raw = await response.text();
      }

      try {
        const parsed = JSON.parse(raw);
        // ✅ Chỉ lấy phần "response_message" trong JSON trả về
        botReply = parsed.response_message || raw;
      } catch {
        botReply = raw;
      }

      const botMessage = { sender: 'bot', text: botReply };
      setMessages((prev) => [...prev, botMessage]);
    } catch (error) {
      console.error('❌ Error sending message:', error);
      const botMessage = {
        sender: 'bot',
        text: '⚠️ Lỗi khi gửi tin nhắn đến server!',
      };
      setMessages((prev) => [...prev, botMessage]);
    } finally {
      setIsSending(false);
    }
  };


  // IMAGE HANDLER
  const handleImageSelect = (e) => {
    const file = e.target.files[0];
    if (file) setSelectedImage(file);
  };
  const handleRemoveImage = () => {
    setSelectedImage(null);
    const imgInput = document.getElementById('chatImageInput');
    if (imgInput) imgInput.value = '';
  };

  // QUIZ BUILDER
  const handleCreateQuizClick = async () => {
    setIsLoading(true);
    setShowQuizLink(false);
    try {
      const formUrl = await createGoogleForm(selectedLang); // ✅ Truyền ngôn ngữ được chọn
      setQuizUrl(formUrl);
      setShowQuizLink(true);
    } catch (error) {
      console.error('Error creating quiz:', error);
      setQuizUrl(error.message);
      setShowQuizLink(true);
    } finally {
      setIsLoading(false);
    }
  };

  //  API  List 
  // thêm ở đầu component
  const [serverFiles, setServerFiles] = useState([]);

  const fetchServerFiles = async () => {
    try {
      const res = await fetch('http://10.1.5.24:8080/api/v1/documents/list');
      const data = await res.json();
      setServerFiles(data.documents || []);
    } catch (err) {
      console.error('❌ Error fetching file list:', err);
    }
  };

  // gọi lần đầu khi load trang
  useEffect(() => {
    fetchServerFiles();
  }, []);


  const handleCopyLink = () => {
    if (quizUrl && !quizUrl.startsWith('Failed to create quiz')) {
      navigator.clipboard.writeText(quizUrl);
      toast.success('Link copied to clipboard!', { position: 'top-right', autoClose: 2000 });
    }
  };

  // FILE UPLOAD

  //  Chỉ cho chọn file Word
  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    const validFiles = files.filter(
      (file) => file.name.endsWith('.doc') || file.name.endsWith('.docx')
    );

    if (validFiles.length === 0) {
      toast.warning('⚠️ Please select only .doc or .docx files!', {
        position: 'top-right',
        autoClose: 2500,
      });
      e.target.value = ''; // reset input
      return;
    }

    setSelectedFiles(validFiles);
  };

  //  Xóa file trong danh sách hiển thị
  const handleRemoveFile = (index) => {
    setSelectedFiles((prev) => prev.filter((_, i) => i !== index));
  };

  //  Upload + kiểm tra định dạng + check status
  const handleUpload = async () => {
    if (selectedFiles.length === 0) {
      toast.warning('⚠️ Please select at least one file to upload!', {
        position: 'top-right',
        autoClose: 2000,
      });
      return;
    }

    // ✅ Kiểm tra lần nữa (an toàn)
    const invalidFiles = selectedFiles.filter(
      (file) => !file.name.endsWith('.doc') && !file.name.endsWith('.docx')
    );
    if (invalidFiles.length > 0) {
      toast.error('❌ Only .doc or .docx files are allowed!', {
        position: 'top-right',
        autoClose: 2500,
      });
      return;
    }

    // ✅ Xóa input ngay khi bắt đầu upload
    const filesToUpload = [...selectedFiles];
    setSelectedFiles([]);
    document.getElementById('fileInput').value = '';

    const formData = new FormData();
    filesToUpload.forEach((file) => formData.append('file', file));

    try {
      // 1️⃣ Upload file
      const res = await fetch('http://10.1.5.24:8080/api/v1/documents/upload', {
        method: 'POST',
        body: formData,
      });

      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      console.log('📤 Upload response:', data);

      // Nếu upload accepted thì bắt đầu check status
      if (data.status === 'accepted' && data.file_id) {
        toast.info('📄 File uploaded. Processing...', {
          position: 'top-right',
          autoClose: 2000,
        });

        // 2️⃣ Hàm check status lặp liên tục
        const checkStatus = async () => {
          try {
            const statusRes = await fetch(
              `http://10.1.5.24:8080/api/v1/documents/${data.file_id}/status`
            );
            if (!statusRes.ok)
              throw new Error(`Status HTTP ${statusRes.status}`);

            const statusData = await statusRes.json();
            console.log('📥 Status:', statusData);

            if (statusData.status === 'completed') {
              toast.success('✅ File processed successfully!', {
                position: 'top-right',
                autoClose: 2000,
              });
              fetchServerFiles();
              return; // dừng lại
            }

            if (statusData.status === 'error' || statusData.status === 'failed') {
              toast.error('❌ File processing failed.', {
                position: 'top-right',
                autoClose: 2500,
              });
              return;
            }

            // Nếu chưa xong thì chờ 3s rồi check lại
            setTimeout(checkStatus, 3000);
          } catch (err) {
            console.error('❌ Error checking status:', err);
            toast.error('⚠️ Error checking file status.', {
              position: 'top-right',
              autoClose: 2500,
            });
          }
        };

        // Gọi lần đầu tiên
        checkStatus();
      } else {
        toast.error('❌ Upload failed!', {
          position: 'top-right',
          autoClose: 2500,
        });
      }
    } catch (err) {
      console.error('❌ Upload error:', err);
      toast.error(`⚠️ Upload error: ${err.message}`, {
        position: 'top-right',
        autoClose: 2500,
      });
    }
  };

  return (
    <main className="main-content-wrapper">
      <ToastContainer />
      {/* <h1 className="main-title1">SecureQuiz Builder & ChatBot</h1> */}

      <div className="content-layout">

        {/* CHATBOT */}
        <div className="column chatbot-column card">
          <h2 className="chatbot-header">
            <FontAwesomeIcon icon={faSearch} className="chatbot-icon" /> Chatbot
          </h2>

          <div className="chat-messages" ref={chatContainerRef}>
            {messages.map((msg, i) => (
              <div key={i} className={`chat-message ${msg.sender === 'user' ? 'user-message' : 'bot-message'}`}>
                {msg.text}
                {msg.image && <img src={msg.image} alt="uploaded" className="chat-image-preview" />}
              </div>
            ))}
          </div>

          <div className="input-box">
            {/* KHỐI <div className="chat-actions"> ĐÃ ĐƯỢC XÓA Ở ĐÂY */}
            <textarea
              placeholder="Ask anything..."
              value={input}
              onChange={e => setInput(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && !e.shiftKey && (e.preventDefault(), handleSend())}
            />
            <button className="send-button" onClick={handleSend} disabled={isSending}>
              <FontAwesomeIcon icon={faPaperPlane} className="send-icon" />
            </button>
          </div>
        </div>

        {/* QUIZ BUILDER */}
        <div className="column quiz-builder-column card small-quiz">
          <h2 className="quiz-header">
            <FontAwesomeIcon icon={faFileAlt} className="quiz-icon" style={{ color: '#e3002b' }} /> SecureQuiz Builder
          </h2>

          <div className="upload-section">
            <div
              className="file-label"
              onClick={() => document.getElementById("fileInput").click()}
            >
              <FontAwesomeIcon icon={faPaperclip} className="file-icon" />
              <span className="file-placeholder">
                {selectedFiles.length > 0 ? (
                  <div className="file-list">
                    {selectedFiles.map((file, index) => (
                      <div key={index} className="file-item">
                        <span className="file-name">{file.name}</span>
                        <FontAwesomeIcon
                          icon={faXmark}
                          className="remove-file-icon"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleRemoveFile(index);
                          }}
                        />
                      </div>
                    ))}
                  </div>
                ) : (
                  "Select file"
                )}
              </span>
            </div>

            <input
              id="fileInput"
              type="file"
              multiple
              style={{ display: "none" }}
              onChange={handleFileChange}
            />

            <button className="upload-button" onClick={handleUpload}>
              <FontAwesomeIcon
                icon={faUpload}
                style={{ marginRight: "6px", color: "white" }}
              />{" "}
              Upload File
            </button>
          </div>

          {/* Danh sách file đã có trên server */}
          <div className="server-file-section">
            <h3 className="server-file-title">📂 Uploaded Files</h3>

            {serverFiles.length === 0 ? (
              <p className="no-files">No files uploaded yet.</p>
            ) : (
              <>
                <div className="server-file-list">
                  {serverFiles.map((file, index) => (
                    <div key={index} className="server-file-item">
                      <span className="server-file-name">{file.name}</span>
                      <button
                        className="delete-file-btn"
                        onClick={() => handleDeleteFile(file.file_id, file.name)}
                      >
                        <FontAwesomeIcon icon={faTrash} />
                      </button>
                    </div>
                  ))}
                </div>


                <div className="delete-all-wrapper">
                  <button className="delete-all-btn" onClick={handleDeleteAll}>
                    <FontAwesomeIcon icon={faTrash} style={{ marginRight: '6px' }} />
                    Delete All
                  </button>
                </div>

              </>
            )}
          </div>







          <div className="quiz-action-section">
            <p className="quiz-message">Let's test your knowledge!</p>

            <div className="quiz-controls">
              {/* Select ngôn ngữ */}
              <select
                className="quiz-lang-select"
                value={selectedLang}
                onChange={(e) => setSelectedLang(e.target.value)}
              >
                <option value="Japanese">Japanese</option>
                <option value="English">English</option>
                <option value="Vietnamese">Vietnamese</option>
              </select>

              {/* Nút tạo quiz */}
              <button
                className="create-quiz-button"
                onClick={handleCreateQuizClick}
                disabled={isLoading}
              >
                {isLoading ? (
                  <>
                    <FontAwesomeIcon icon={faSpinner} spin className="quiz-icon-btn" /> Creating Quiz...
                  </>
                ) : (
                  <>
                    <FontAwesomeIcon icon={faPenToSquare} className="quiz-icon-btn" /> Create Quiz
                  </>
                )}
              </button>
            </div>

            {/* Thông báo đang xử lý */}
            {isLoading && (
              <div className="loading-message mt-2 text-red-600 font-medium flex items-center justify-center">
                <FontAwesomeIcon icon={faSpinner} spin className="mr-2 text-red-600" />
                <span className="text-red-600">Creating quiz, please wait a moment...</span>
              </div>

            )}
          </div>

          {/* Khi tạo quiz xong, hiển thị link copy */}
          {showQuizLink && (
            <div className="quiz-link-wrapper">
              <div className="quiz-link-box">
                <p className="quiz-link-text">{quizUrl}</p>
              </div>
              <button
                className="copy-button"
                onClick={handleCopyLink}
                disabled={quizUrl.startsWith('Failed')}
              >
                <FontAwesomeIcon icon={faCopy} className="copy-icon" /> Copy
              </button>
            </div>
          )}

        </div>
      </div>
    </main>
  );
};

export default MainContent;
