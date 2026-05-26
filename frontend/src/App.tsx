import { useState } from 'react'
import './App.css'

function App() {
  const [uploadedFile, setUploadedFile] = useState<File | null>(null)
  const [view, setView] = useState<'upload' | 'results'>('upload')
  const [analysisResults, setAnalysisResults] = useState({
    score: 0,
    feedback: [] as string[],
    strengths: [] as string[],
    improvements: [] as string[]
  })

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (file && file.type === 'application/pdf') {
      setUploadedFile(file)
    } else {
      alert('Please upload a PDF file')
    }
  }

  const analyzeResume = () => {
    // Mock analysis - you'll connect this to your backend later
    setAnalysisResults({
      score: 85,
      feedback: [
        'Strong technical skills section',
        'Clear project descriptions',
        'Missing quantifiable achievements'
      ],
      strengths: [
        'ATS-friendly formatting',
        'Relevant keywords present',
        'Good use of action verbs'
      ],
      improvements: [
        'Add more measurable results',
        'Include more industry keywords',
        'Expand on leadership experience'
      ]
    })
    setView('results')
  }

  if (view === 'results') {
    return (
      <div className="app-container">
        {/* Navigation */}
        <nav className="navbar">
          <div className="nav-brand">
            <h2>Resuminator</h2>
          </div>
          <div className="nav-links">
            <span>ATS + Description</span>
          </div>
        </nav>

        {/* Results Page */}
        <div className="results-container">
          <aside className="sidebar">
            <div className="score-card">
              <h3>Overall Score</h3>
              <div className="score-circle">
                <span className="score-number">{analysisResults.score}</span>
                <span className="score-label">/100</span>
              </div>
            </div>

            <div className="section">
              <h4>People History</h4>
              <div className="history-item">Previous scan - 78/100</div>
              <div className="history-item">Initial resume - 65/100</div>
            </div>

            <div className="section">
              <h4>Prompts</h4>
              <div className="prompt-input">
                <input type="text" placeholder="Ask about your resume..." />
                <button>→</button>
              </div>
            </div>

            <button className="back-btn" onClick={() => setView('upload')}>
              ← Back to Upload
            </button>
          </aside>

          <main className="resume-viewer">
            <div className="viewer-header">
              <h3>Resume Analysis</h3>
              <div className="tabs">
                <button className="tab active">Overview</button>
                <button className="tab">Parsed Text</button>
              </div>
            </div>

            <div className="analysis-content">
              <section className="analysis-section">
                <h4>Strengths</h4>
                <ul>
                  {analysisResults.strengths.map((item, idx) => (
                    <li key={idx}>{item}</li>
                  ))}
                </ul>
              </section>

              <section className="analysis-section">
                <h4>Areas for Improvement</h4>
                <ul>
                  {analysisResults.improvements.map((item, idx) => (
                    <li key={idx}>{item}</li>
                  ))}
                </ul>
              </section>

              <section className="analysis-section">
                <h4>Detailed Feedback</h4>
                <ul>
                  {analysisResults.feedback.map((item, idx) => (
                    <li key={idx}>{item}</li>
                  ))}
                </ul>
              </section>

              <div className="resume-preview">
                <p className="preview-label">Your Resume Preview:</p>
                <div className="preview-box">
                  <p>📄 {uploadedFile?.name}</p>
                  <p className="preview-text">
                    Resume content would be displayed here after parsing...
                  </p>
                </div>
              </div>
            </div>
          </main>
        </div>
      </div>
    )
  }

  // Upload Page
  return (
    <div className="app-container">
      <nav className="navbar">
        <div className="nav-brand">
          <h2>Resuminator</h2>
        </div>
        <div className="nav-links">
          <span>ATS + Description</span>
        </div>
      </nav>

      <main className="upload-page">
        <div className="hero-section">
          <h1>Optimize Your Resume for ATS</h1>
          <p>Upload your resume and get instant feedback on how it performs with Applicant Tracking Systems</p>
        </div>

        <div className="upload-section">
          <div className="upload-box">
            <input
              type="file"
              id="resume-upload"
              accept=".pdf"
              onChange={handleFileUpload}
              style={{ display: 'none' }}
            />
            <label htmlFor="resume-upload" className="upload-label">
              {uploadedFile ? (
                <>
                  <span className="file-icon">📄</span>
                  <p>{uploadedFile.name}</p>
                  <p className="file-size">
                    {(uploadedFile.size / 1024).toFixed(2)} KB
                  </p>
                </>
              ) : (
                <>
                  <span className="upload-icon">⬆️</span>
                  <p>Click to upload or drag and drop</p>
                  <p className="upload-hint">PDF files only</p>
                </>
              )}
            </label>
          </div>

          {uploadedFile && (
            <button className="analyze-btn" onClick={analyzeResume}>
              Analyze Resume →
            </button>
          )}
        </div>

        <div className="features">
          <div className="feature-card">
            <span className="feature-icon">🎯</span>
            <h3>ATS Score</h3>
            <p>Get a comprehensive score on how well your resume will perform</p>
          </div>
          <div className="feature-card">
            <span className="feature-icon">💡</span>
            <h3>Smart Suggestions</h3>
            <p>Receive actionable feedback to improve your resume</p>
          </div>
          <div className="feature-card">
            <span className="feature-icon">🚀</span>
            <h3>Instant Results</h3>
            <p>Get your analysis in seconds, not hours</p>
          </div>
        </div>
      </main>
    </div>
  )
}

export default App
